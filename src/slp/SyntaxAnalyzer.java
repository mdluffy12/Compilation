package slp;

import java.io.IOException;
import java.util.ArrayList;

import jdk.nashorn.internal.ir.BlockStatement;
import symbolTypes.*;
import slp.BlockStmt;
import slp.ClassField;
import slp.Environment;
import slp.ICClass;
import slp.LocalVar;
import slp.MethodFormal;
import slp.ObjectClassType;
import slp.PrimitiveType;
import slp.Program;
import slp.StaticMethod;
import slp.Stmt;
import slp.VirtualMethod;

/** Evaluates straight line programs.
 */
public class SyntaxAnalyzer implements PropagatingVisitor<Environment, symbolTypes.SymbolType> 
{

	protected Program root;
	/** Constructs an SLP interpreter for the given AST.
	 * 
	 * @param root An SLP AST node.
	 */
	public SyntaxAnalyzer(ASTNode root) {
		this.root = (Program)root;
	}
	
	/** Interprets the AST passed to the constructor.
	 */
	public void Analyze() {
		Environment env = new Environment();
		//System.out.println("Inserting All Classess Information");
		InsertLibraryClassInformation(env);
		InsertAllClassInformation(root, env);
		//System.out.println("Checking Main");
		containsMain(root);
		//System.out.println("Going Over Main Syntax Analysis");
		root.accept(this, env);
		//System.out.println("Finished Syntax Analysis Successfully");
	}
	
	private void InsertLibraryClassInformation(Environment env)
	{
		ClassType libType = new ClassType("Library", null);
		env.symbolTable.InsertNewDeclerationAsBothStaticAndVirtual("Library", libType);
		SymbolType retType;
		SymbolType[] argsTypes;
		
		retType = new VoidType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new StringType(true);
		libType.AddNewMember("println", new MethodType(argsTypes, retType, true));
		
		retType = new VoidType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new StringType(true);
		libType.AddNewMember("print", new MethodType(argsTypes, retType, true));
		
		retType = new VoidType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new IntType(true);
		libType.AddNewMember("printi", new MethodType(argsTypes, retType, true));
		
		retType = new VoidType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new BoolType(true);
		libType.AddNewMember("printb", new MethodType(argsTypes, retType, true));
		
		retType = new IntType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new VoidType(true);
		libType.AddNewMember("readi", new MethodType(argsTypes, retType, true));
		
		retType = new StringType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new VoidType(true);
		libType.AddNewMember("readln", new MethodType(argsTypes, retType, true));

		retType = new BoolType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new VoidType(true);
		libType.AddNewMember("eof", new MethodType(argsTypes, retType, true));

		retType = new IntType(true);
		argsTypes = new SymbolType[2];
		argsTypes[0] = new StringType(true);
		argsTypes[1] = new IntType(true);
		libType.AddNewMember("stoi", new MethodType(argsTypes, retType, true));

		retType = new StringType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new IntType(true);
		libType.AddNewMember("itos", new MethodType(argsTypes, retType, true));
		
		retType = new ArrType(new IntType(true), 1);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new StringType(true);
		libType.AddNewMember("stoa", new MethodType(argsTypes, retType, true));

		retType = new StringType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new ArrType(new IntType(true), 1);
		libType.AddNewMember("atos", new MethodType(argsTypes, retType, true));
		
		retType = new IntType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new IntType(true);
		libType.AddNewMember("random", new MethodType(argsTypes, retType, true));
		
		retType = new IntType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new VoidType(true);
		libType.AddNewMember("time", new MethodType(argsTypes, retType, true));
		
		retType = new VoidType(true);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new IntType(true);
		libType.AddNewMember("exit", new MethodType(argsTypes, retType, true));
	}
	
	private void InsertAllClassInformation(Program prog1, Environment env) {
		
		
		for (ICClass cls : prog1.getClasses()) //first pass insert all classes
		{
			ClassType parentClassType = null;
			if(cls.hasSuperClass())
			{
				SymbolType t1 = env.symbolTable.GetClosestVarWithSameName(cls.getSuperClassName());
				if(t1 == null)
				{
					throw new RuntimeException(cls.getLine()+
							":" + "Super class " + cls.getSuperClassName() + " is not defined!");
				}
				parentClassType = (ClassType)t1;
			}
			
			if(env.symbolTable.GetClosestVarWithSameName(cls.getName()) != null)
			{
				throw new RuntimeException(cls.getLine()+
						":" + "Class " + cls.getName() + " is already defined!");
			}
			
			ClassType clsType = new ClassType(cls.getName(), parentClassType);
			//we are inserting to both because classes themselves should exists everywhere
			env.symbolTable.InsertNewDeclerationAsBothStaticAndVirtual(cls.getName(), clsType);
		}
		
		//second pass insert all members and functions;
		for (ICClass cls : prog1.getClasses())
		{
			ClassType clsType = (ClassType)env.symbolTable.GetClosestVarWithSameName(cls.getName());
			
			for (ClassMember member : cls.getMembers())
			{
				SymbolType memberType = null;
				if(member instanceof ClassField)
				{
					ClassField field = (ClassField)member;
					if(clsType.GetMemberFromMeOrClosestParent(field.getName()) != null)
					{
						throw new RuntimeException(field.getLine()+
								":" + "Field member already defined in the class or parent class. hiding or redefinition is not allowed.");
					}
					memberType = SymbolType.getTypeFromAST(field.getType(), env.symbolTable);
					if(memberType == null)
					{
						throw new RuntimeException(field.getLine()+
								":" + "Unknown member type " + field.getType().getName());
					}
					clsType.AddNewMember(field.getName(), memberType);
				}
				else
				{
					
					
					ClassMethod met1 = (ClassMethod)member;
					memberType = SymbolType.createMethType(met1, env.symbolTable);
					
					if(clsType.GetMemberFromCurrentClassOnly(met1.getName()) != null)
					{
						throw new RuntimeException(member.getLine()+
								":" + "A method with the same name is already defined in this class method overloading or redifining is not allowed");
					}
					SymbolType overriddenMethod = clsType.GetMemberFromMeOrClosestParent(met1.getName());
					if(overriddenMethod != null)
					{
						if(overriddenMethod.compareType(memberType) == false)
						throw new RuntimeException(member.getLine()+
								":" + "cannot define the method " + memberType.getName() + " with the type " + memberType.toString() + 
								" as an override to the type " + overriddenMethod.toString() + " the methods doesn't have the same signature or the other is a var");
					}
					clsType.AddNewMember(met1.getName(), memberType);
				}
			}
		}
	}

	private void containsMain(Program program) {
		int mainCount = 0;
		int iLineNumber = 0;
		ClassMethod mainMethod = null;
		for (ICClass cls : program.getClasses()) {
			for (ClassMember member : cls.getMembers())
			{
				if (member instanceof ClassMethod)
				{
					ClassMethod method = (ClassMethod)member;
					if (method.getName().equals("main")) 
					{
						mainMethod = method;
						++mainCount;
						iLineNumber = method.getLine();
					}
				}
			}
		}
		if (mainCount == 0) 
		{
			throw new RuntimeException("Main method is undeclared.");
		}
		if (mainCount != 1)
		{
			throw new RuntimeException("Main method declared multiple times.");
		}
		
		if(mainMethod instanceof VirtualMethod)
		{
			throw new RuntimeException(iLineNumber + ":main must be a static function and not a virtual one");
		}

		if(mainMethod.getFormals().size() != 1)
		{
			throw new RuntimeException(iLineNumber + ":Only 1 argument is allowed for main function");
		}
		
		slp.Type argsType = mainMethod.getFormals().get(0).getType();
		if(!argsType.getName().equals("string") ||
		   argsType.getDimension() != 1)
		{
			throw new RuntimeException(iLineNumber + ":The main function argument must be from the type String[]");
		}
	}
	
	public SymbolType visit(StmtList stmts, Environment env) 
	{
		for (Stmt st : stmts.statements) 
		{
			st.accept(this, env);
		}
		return null;
	}

	public SymbolType visit(Stmt stmt, Environment env) 
	{
		throw new RuntimeException("Unexpected visit of Stmt!");
	}

	public SymbolType visit(AssignStmt stmt, Environment env) 
	{
		SymbolType type1 = stmt.varExpr.accept(this, env);
		SymbolType type2 = stmt.rhs.accept(this, env);
		
		if (type1 == null || type2 == null) 
		{
			throw new RuntimeException(stmt.getLine()+
					": can not perform assignment from type void, or to type void.");
		}
		
		if(!type1.compareType(type2))
		{
			throw new RuntimeException(stmt.getLine()+
					": can not perform assignment from type "+type2.toString() +" to type "+type1.toString());
		}
		
		if(!type2.isInitialized())
		{
			throw new RuntimeException(stmt.rhs.getLine()+
					": "+stmt.rhs.toString()+" is not initialzied.");
		}
		type1.initialize();
		return null;
	}

	public SymbolType visit(newArray arr, Environment env) {
		SymbolType typeSize = arr.length.accept(this, env);

		if (typeSize == null || !typeSize.isIntType()) {
			throw new RuntimeException(arr.getLine()+
					": Size of array must be an integer number.");
		}

		SymbolType t1 = arr.type.accept(this, env);
		if(t1 instanceof ArrType)
		{
			ArrType art1 = (ArrType)t1;
			return new ArrType(art1.getElemType(), art1.getDimension() + 1, true);
		}
		return new ArrType(t1, 1,true);
	}

	public SymbolType visit(UnaryOpExpr expr, Environment env) 
	{
		SymbolType type = expr.operand.accept(this, env);
		// Check for semantic error
		if (type == null) 
		{
			throw new RuntimeException(expr.getLine()+
					": can not perform unary operation on a VOID type parameter.");
		}
		if(!type.isInitialized())
		{
			throw new RuntimeException(expr.getLine()+
					": "+expr.operand.toString()+" is not initialzied.");
		}
		switch (expr.op) 
		{
			case UMINUS:
				if (type.isIntType()) 
				{
					return new symbolTypes.IntType(true);
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid type in an unary minus opertaion.");
				}
			case LNEG:
				if (type.isBoolType())
				{
					return new symbolTypes.BoolType(true);
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid type in an unary 'not' opertaion.");
				}
			default:
				throw new RuntimeException(expr.getLine()+
						": Invalid type in an unary opertaion.");
		}
	}

	public SymbolType visit(BinaryOpExpr expr, Environment env) 
	{
		SymbolType type1 = expr.lhs.accept(this, env);
		SymbolType type2 = expr.rhs.accept(this, env);
		
		if (type1 == null || type2 == null) {
			throw new RuntimeException(expr.getLine()+
					": can not perform binary operation on a VOID type parameter.");
		}
		
		if(!type1.isInitialized())
		{
			throw new RuntimeException(expr.getLine()+
					": "+expr.lhs.toString()+" is not initialzied.");
		}
		if(!type2.isInitialized())
		{
			throw new RuntimeException(expr.getLine()+
					": "+expr.rhs.toString()+" is not initialzied.");
		}

		switch (expr.op) 
		{
			case PLUS:
				if (type1.isIntType() && type2.isIntType()) 
				{
					return new symbolTypes.IntType(true);
				}
				else if (type1.isStringType() && type2.isStringType())
				{
					return new symbolTypes.StringType(true);
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid  usage of a arithmetic operation <" + expr.op.toString() +">" );
				}
			case MINUS:
			case MULT:
			case DIV:
			case MOD:
				if (type1.isIntType() && type2.isIntType()) {
					return new symbolTypes.IntType(true);
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid  usage of a arithmetic operation <" + expr.op.toString() +">." );
				}
			case LT:
			case GT:
			case LE:
			case GE:
				if (type1.isIntType() && type2.isIntType())
				{
					return new symbolTypes.BoolType(true);
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
						": Invalid  usage of a binary operation '"+ expr.op.toString()+
						"' with a non-integer expression. expressions are left: " + type1.toString() + " right: " + type2.toString());
				}
			case LAND:
			case LOR:
				if (type1.isBoolType() && type2.isBoolType())
				{
					return new symbolTypes.BoolType(true);
				}
				else
				{
				throw new RuntimeException(expr.getLine()+
						": Invalid usage of a binary operation <"+ expr.op.toString()+ "> with a non-boolean expression.");
				}
			case NEQUAL:
			case EQUAL:
				if (type1.equals(type2))
					return new symbolTypes.BoolType(true);
				if ((type1.isNullable()) && (type2.isNullType()) || (type1.isNullType()) && (type2.isNullable()))
					return new symbolTypes.BoolType(true);
				if ((type1.subTypeOf(type2)) || (type2.subTypeOf(type1)))
					return new symbolTypes.BoolType(true);
				throw new RuntimeException(expr.getLine()+
						": Invalid usage of a binary operation <"+ expr.op.toString()+ "> with a mismatching expression.");
			default:
				throw new RuntimeException(expr.getLine()+
						": Invalid use of a binary operation.");
		}
	}
	
	public SymbolType visit(Program prog, Environment env)
	{	
		for (ICClass cls : prog.getClasses()) 
		{
			cls.accept(this, env); // get all the relevant methods and variables of the class
		}

		return null;
	}
	
	public SymbolType visit(ICClass cls, Environment env)
	{
		String classId = cls.getName();
		env.symbolTable.StartScope();
		ClassType classType = (ClassType) env.symbolTable.GetClosestVarWithSameName(classId);
		env.symbolTable.InsertNewDecleration("This", classType);
		env.currentClassName = classId;
		for (ClassMember member : cls.getMembers()) {

			// Initialize needed parameters:
			if (member instanceof ClassField)
			{
				ClassField field = (ClassField)member;
				String fieldId = field.getName();
				symbolTypes.SymbolType fieldType = classType.GetMemberFromCurrentClassOnly(fieldId);
				if(fieldType == null)
				{
					throw new RuntimeException("internal error symbol table is corrupted");
				}
				// add the field to the symbol table
				if(env.symbolTable.IsDeclaredInCurrentScope(fieldId))
				{
					throw new RuntimeException(field.getLine()+": field "+ field.getName() +" is declared more than once");
				}
				fieldType.initialize();
				env.symbolTable.InsertNewDecleration(fieldId, fieldType);
				
				field.accept(this, env);
				
			}
			if (member instanceof ClassMethod)
			{
				ClassMethod method = (ClassMethod)member;
				String methodId = method.getName();
				symbolTypes.SymbolType methodType = classType.GetMemberFromCurrentClassOnly(methodId);
						
				// add the method to the symbol table
				if (env.symbolTable.IsDeclaredInCurrentScope(methodId))
				{
					throw new RuntimeException(method.getLine()+": method "+ method.getName() +" is declared more than once");
				}
				if(method instanceof StaticMethod)
				{//this is a static method it can be called from static functions as well
					env.symbolTable.InsertNewDeclerationAsBothStaticAndVirtual(methodId, methodType);
				}
				else
				{
					env.symbolTable.InsertNewDecleration(methodId, methodType);
				}
				method.accept(this, env);
			}
			

		}
		env.symbolTable.ExitScope();
		return null;
	}
	
	public SymbolType visit(ClassField field, Environment env)
	{
		return null;
	}
	
	public SymbolType visit(MethodFormal formal, Environment env)
	{
		String formalId = formal.getName();
		SymbolType formalType = SymbolType.getTypeFromAST(formal.getType(), env.symbolTable);
		if(formalType == null)
		{
			throw new RuntimeException(formal.getLine()+":Unknown formal type " + formalId);
		}
		
		if (!env.symbolTable.IsDeclaredInCurrentScope(formalId))
		{
			env.symbolTable.InsertNewDecleration(formalId, formalType);
			formalType.initialize();
			return formalType;
		}
		else
		{
			throw new RuntimeException(formal.getLine()+": formal "+formalId+" is declered more than once.");
		}
	}
	
	public SymbolType visit(StaticMethod method, Environment env)
	{
		env.symbolTable.StartStaticMode();
		visitMethod(method, env);
		env.symbolTable.StartVirtualMode();
		return null;
	}
	
	public SymbolType visit(VirtualMethod method, Environment env)
	{
		visitMethod(method, env);
		return null;
	}
	
	public void visitMethod(ClassMethod method, Environment env)
	{
		env.symbolTable.StartScope();
		for (MethodFormal formal : method.getFormals())
		{
			SymbolType formalType = formal.accept(this, env);
			env.symbolTable.InsertNewDecleration(formal.getName(), formalType);
		}
		env.currentMethod = SymbolType.createMethType(method, env.symbolTable) ;
		env.has_return_in_every_path = false;
		for (Stmt s : method.getStatements().statements)
		{
			s.accept(this, env);
		}
		if(env.has_return_in_every_path == false && env.currentMethod.getRetType().isVoidType() == false)
		{
			throw new RuntimeException(method.getLine()+
					": Function does not have a return in every path and it is not a void type");
		}
		env.currentMethod = null;
		method.getType().accept(this, env);
		env.symbolTable.ExitScope();
	}
	
	public SymbolType visit(BlockStmt blk, Environment env)
	{
		env.symbolTable.StartScope();
		
		for (Stmt stmnt : blk.m_stmtList.statements)
		{
			stmnt.accept(this, env);
		}
		
		env.symbolTable.ExitScope();
		return null;
	}
	
	public SymbolType visit(CallStmt call, Environment env)
	{
		call.getCall().accept(this, env);
		return null;
	}
	
	public SymbolType visit(If stmt, Environment env) 
	{
		SymbolType sym_type = stmt.getCondition().accept(this, env);
		if(!sym_type.isBoolType())
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid expression inside if, condition is not of boolean type.");
		}
		if (!sym_type.isInitialized())
		{
			throw new RuntimeException(stmt.getCondition().getLine()+
					": "+stmt.getCondition().toString()+" is not initialzied.");

		}
		
		boolean has_return_before = env.has_return_in_every_path;
		env.has_return_in_every_path = false;
		stmt.getOperation().accept(this, env);
		boolean has_return_in_if = env.has_return_in_every_path;
		boolean has_return_in_else = false;
		
		// Validate "else" statement
		if (stmt.hasElse()) 
		{
			env.has_return_in_every_path = false;
			stmt.getElseOperation().accept(this, env);
			has_return_in_else = env.has_return_in_every_path;
		}
		
		env.has_return_in_every_path = (has_return_before) || (has_return_in_if && has_return_in_else);
		return null;
	}
	
	public SymbolType visit(While stmt, Environment env) 
	{
		SymbolType sym_type = stmt.getCondition().accept(this,env);
		if(!sym_type.isBoolType())
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid expression inside While, condition is not of boolean type.");
		}
		if (!sym_type.isInitialized())
		{
			throw new RuntimeException(stmt.getCondition().getLine()+
					": "+stmt.getCondition().toString()+" is not initialzied.");

		}
		boolean has_return_before = env.has_return_in_every_path;
		env.loop_counter += 1;
		stmt.getOperation().accept(this, env);
		env.loop_counter -= 1;
		env.has_return_in_every_path = has_return_before;
		return null;
	}
	
	public SymbolType visit(Break stmt, Environment env)
	{
		if (env.loop_counter == 0)
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid  usage of a break statement not inside of loop");
		}
		return null;
	}

	public SymbolType visit(Continue stmt,  Environment env)
	{
		if (env.loop_counter == 0)
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid  usage of a continue statement not inside of loop");
		}
		return null;
	}
	
	@Override
	public SymbolType visit(PrimitiveType ptype, Environment d) {
		// TODO Auto-generated method stub
		return SymbolType.getTypeFromAST(ptype, d.symbolTable);
	}

	@Override
	public SymbolType visit(ObjectClassType octype, Environment d) {
		// TODO Auto-generated method stub
		SymbolType t1 =  SymbolType.getTypeFromAST(octype, d.symbolTable);
		if(t1 == null)
		{
			throw new RuntimeException(octype.getLine()+
					":" + "Unknown type " + octype.getName());
		}
		return t1;
	}

	@Override
	public SymbolType visit(LocalVar lvar, Environment env) {
		if (env.symbolTable.IsDeclaredInCurrentScope(lvar.getName()))
		{
			throw new RuntimeException(lvar.getLine()+
					": a variable already declared in the current scope with this name " + lvar.getName());
		}
		SymbolType type1 = env.symbolTable.GetClosestVarWithSameName(lvar.getName());
		if(type1 != null && type1 instanceof MethodType)
		{
			throw new RuntimeException(lvar.getLine()+
					": it is not allowed to define a function and a variable with the same name '" + lvar.getName() + "'");
		}
		int roni = 0;
		if (lvar.getName().equals("dana")){
			roni++;
		}
		
		
		SymbolType varType = lvar.getType().accept(this, env);
		env.symbolTable.InsertNewDecleration(lvar.getName(), varType);
		System.out.println("Init " + lvar.getName() + roni);
		if(lvar.hasInitValue())
		{
			System.out.println("Init " + lvar.getName() + " val: " + lvar.getInitValue());
			SymbolType initValType = lvar.getInitValue().accept(this, env);
			if(initValType.subTypeOf(varType) == false)
			{
				throw new RuntimeException(lvar.getLine()+
						": it is not allowed to initialize a var with type " + varType.toString() + " with type " + initValType.toString() + " which is not a subtype");
			}
		}
		return null;
	}

	@Override
	public SymbolType visit(BoolLiteral bl, Environment d) {
		return new BoolType(true);
	}

	@Override
	public SymbolType visit(ExprLength exprLength, Environment d) {
		SymbolType type1 = exprLength.e.accept(this, d);
		if(!(type1 instanceof ArrType))
		{
			throw new RuntimeException(exprLength.getLine()+
					": Length is only accepted to use as part of an array! and this expression is not an array it is of type " + type1.toString());
		}
		if(!type1.isInitialized())
		{

			throw new RuntimeException(exprLength.e.getLine()+
					": "+exprLength.e.toString()+" is not initialzied.");
		}
		return new IntType(true);
	}

	@Override
	public SymbolType visit(IntLiteral intLit, Environment d) {
		return new IntType(true);
	}

	@Override
	public SymbolType visit(newClass nclss, Environment d) {
		SymbolType type1 = d.symbolTable.GetClosestVarWithSameName(nclss.classID);
		if(type1 == null)
		{
			throw new RuntimeException(nclss.getLine()+
					": there is no class of with the name " + nclss.classID);
		}
		type1.initialize();
		return type1;
	}

	@Override
	public SymbolType visit(NullLiteral nllLit, Environment d) {
		return new NullType(true);
	}

	@Override
	public SymbolType visit(Return retStmt, Environment d) {
		if(d.currentMethod == null)
		{
			throw new RuntimeException(retStmt.getLine()+
					": using return outside of function is illegal! ");
		}
		//if we void function but we return something
		if(d.currentMethod.getRetType().isVoidType() == true && retStmt.hasValue() == true)
		{
			throw new RuntimeException(retStmt.getLine()+
					": return inside void function must not return an expression");
		}
		
		if(d.currentMethod.getRetType().isVoidType() == false && retStmt.hasValue() == false)
		{
			throw new RuntimeException(retStmt.getLine()+
					": return inside a function with return value of type " + d.currentMethod.getRetType().toString() + 
					" but return returning type void");
		}
		d.has_return_in_every_path = true;
		
		if(retStmt.hasValue())
		{
			SymbolType returnExprType = retStmt.getValue().accept(this, d);
			
			if(returnExprType.subTypeOf(d.currentMethod.getRetType()) == false)
			{
				throw new RuntimeException(retStmt.getLine()+
						": function and return type mismatch. type(return) " + returnExprType.toString() + 
						" is not a subtype of function return type " + d.currentMethod.getRetType().toString());
			}
			if (!returnExprType.isInitialized())
			{
				throw new RuntimeException(retStmt.getLine()+
						": "+retStmt.getValue().toString()+" is not initialzied.");

			}
			return returnExprType;
		}
		return new VoidType();
	}

	@Override
	public SymbolType visit(StaticFunctionCall sfc, Environment d) {
		ClassType clssT1 = (ClassType) d.symbolTable.GetClosestVarWithSameName(sfc.classID);
		if(clssT1 == null)
		{
			throw new RuntimeException(sfc.getLine()+
					": name of the class used in the static function call does not exists " + sfc.classID);
		}
		SymbolType funcType = clssT1.GetMemberFromMeOrClosestParent(sfc.funcID);
		if(funcType == null)
		{
			throw new RuntimeException(sfc.getLine()+
					": class " + sfc.classID + " does not have a member named " + sfc.funcID);
		}
		if(!(funcType instanceof MethodType))
		{
			throw new RuntimeException(sfc.getLine()+
					": class " + sfc.classID + " member name " + sfc.funcID + " is not a method! you cannot call it as if it is");
		}
		MethodType ftype1 = (MethodType)funcType;
		ArrayList<SymbolType> parTypes = new ArrayList<SymbolType>();
		ArgumentList l1 = sfc.args;
		while(l1 != null)
		{
			parTypes.add(l1.data.accept(this, d));
			l1 = l1.next;
		}
		if(parTypes.size() == 0)
		{
			parTypes.add(new VoidType());
		}
		SymbolType[] params = new SymbolType[parTypes.size()];
		params = parTypes.toArray(params);
		MethodType ftype2 = new MethodType(params, ftype1.getRetType(), true);
		if(ftype1.getIsStatic() == false)
		{
			throw new RuntimeException(sfc.getLine()+
					": class " + sfc.classID + " member name " + sfc.funcID + " is not a static but you call it as if it was");
		}
		
		if(ftype2.subFunctionOf(ftype1) == false)
		{
			throw new RuntimeException(sfc.getLine()+
					": class " + sfc.classID + " member name " + sfc.funcID +
					"  is of type " + ftype1.toString() + " but expected function of type(because of the parameters) " + ftype2.toString());
		}
		return ftype2.getRetType();
	}

	@Override
	public SymbolType visit(VirtualFunctionCall vfc, Environment d) {
		SymbolType t1 = null;
		boolean bMustBeStatic = false;
		if(vfc.prefixExpr == null)
		{
			t1 = d.symbolTable.GetClosestVarWithSameName("This");
			if(t1 == null)
			{
				//maybe its a static function call from a static function (implicit one without CLASSNAME.
				t1 = d.symbolTable.GetClosestVarWithSameName(d.currentClassName);
				bMustBeStatic = true;
			}
		}
		else
		{
			t1 = vfc.prefixExpr.accept(this, d);
			if(!(t1 instanceof ClassType))
			{
				throw new RuntimeException(vfc.getLine()+
						": expression is not a class and has no functions. it is of type " + t1.toString());
			}
		}
		
		ClassType clssT1 = (ClassType)t1;
		SymbolType funcType = clssT1.GetMemberFromMeOrClosestParent(vfc.funcID);
		if(funcType == null)
		{
			throw new RuntimeException(vfc.getLine()+
					": class " + clssT1.toString() + " does not have a member named " + vfc.funcID);
		}
		if(!(funcType instanceof MethodType))
		{
			throw new RuntimeException(vfc.getLine()+
					": class " + clssT1.toString() + " member name " + vfc.funcID + " is not a method! you cannot call it as if it is");
		}
		MethodType ftype1 = (MethodType)funcType;
		ArrayList<SymbolType> parTypes = new ArrayList<SymbolType>();
		ArgumentList l1 = vfc.args;
		while(l1 != null)
		{
			parTypes.add(l1.data.accept(this, d));
			l1 = l1.next;
		}
		if(parTypes.size() == 0)
		{
			parTypes.add(new VoidType());
		}
		SymbolType[] params = new SymbolType[parTypes.size()];
		params = parTypes.toArray(params);
		MethodType ftype2 = new MethodType(params, ftype1.getRetType(), ftype1.getIsStatic(), true);
		if(ftype1.getIsStatic() == true)
		{
			if(vfc.prefixExpr != null)
			{
				throw new RuntimeException(vfc.getLine()+
						": class " + clssT1.toString() + " member name " + vfc.funcID + " is  static but you call it as if it wasn't");
			}
		}
		else if(bMustBeStatic)
		{
			throw new RuntimeException(vfc.getLine()+
					": cannot call virtual function inside static function");
		}
		
		if(ftype2.subFunctionOf(ftype1) == false)
		{
			throw new RuntimeException(vfc.getLine()+
					": class " + clssT1.toString() + " name " + vfc.funcID +
					"  is of type " + ftype1.toString() + " but expected function of type(because of the parameters) " + ftype2.toString());
		}
		return ftype2.getRetType();
	}
	
	@Override
	public SymbolType visit(StringLiteral slit, Environment d) {
		return new StringType(true);
	}

	@Override
	public SymbolType visit(This thisStmt, Environment d) {
		SymbolType thisType = d.symbolTable.GetClosestVarWithSameName("This");
		if(thisType == null)
		{
			throw new RuntimeException(thisStmt.getLine()+
					": 'this' could not been found at this context make sure you are inside virtual method of a class");
		}
		return thisType;
	}

	@Override
	public SymbolType visit(VarValueLocation varValLoc, Environment d) {
		SymbolType t1 = null;
		if(varValLoc.UseVarExpr)
		{
			SymbolType t2 = varValLoc.varExpression.accept(this, d);
			if(!(t2 instanceof ClassType))
			{
				throw new RuntimeException(varValLoc.getLine()+
						": you cannot take an attribue of not a class type (using '.')"
						+ "the expression value was " + t2.toString());
			}
			ClassType clsType = (ClassType) t2;
			t1 = clsType.GetMemberFromMeOrClosestParent(varValLoc.varID);
			if(t1 == null)
			{
				throw new RuntimeException(varValLoc.getLine()+
						": class" + t2.toString() + " has no member variable with the name " + varValLoc.varID);
			}
			if(t1 instanceof MethodType)
			{
				throw new RuntimeException(varValLoc.getLine()+
						": class" + t2.toString() + " member  " + varValLoc.varID + " is a function but used like variable!");
			}
			return t1;
		}
		else
		{
			t1 = d.symbolTable.GetClosestVarWithSameName(varValLoc.varID);
			if(t1 == null)
			{
				ClassType clsThis = (ClassType) d.symbolTable.GetClosestVarWithSameName("This"); //check maybe it is implicit this var
				if(clsThis != null)
				{
					t1 = clsThis.GetMemberFromMeOrClosestParent(varValLoc.varID);
					if(t1 != null)
					{
						return t1;
					}
				}
				throw new RuntimeException(varValLoc.getLine()+
						": no variable with the name " + varValLoc.varID + " exists");
			}
			return t1;
		}
	}

	@Override
	public SymbolType visit(ArrValueLocation arrValLoc, Environment d) {
		SymbolType arrType = arrValLoc.baseExpression.accept(this, d);
		SymbolType indType = arrValLoc.indexExpression.accept(this, d);
		if(arrType.isArrType() == false)
		{
			throw new RuntimeException(arrValLoc.getLine()+
					": trying to use [] on a not array object. object is from type " + arrType.toString());
		}
		if(indType.isIntType() == false)
		{
			throw new RuntimeException(arrValLoc.getLine()+
					": trying to use [] with index of not type int. object is from type " + indType.toString());
		}
		
		ArrType arrT = (ArrType)arrType;
		if(arrT.getDimension() == 1) //it is the last dimension we should return a not array type
		{
			return arrT.getElemType();
		}
		return new ArrType(arrT.getElemType(), arrT.getDimension() - 1, true);
	}
}