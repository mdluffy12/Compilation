package slp;

import java.io.IOException;

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
		System.out.println("Inserting All Classess Information");
		InsertAllClassInformation(root, env);
		System.out.println("Checking Main");
		containsMain(root);
		System.out.println("Going Over Main Syntax Analysis");
		root.accept(this, env);
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
						if(overriddenMethod.toString().equals(memberType.toString()) == false)
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
		return null;
	}

	
	public SymbolType visit(newArray arr, Environment env) {
		SymbolType typeSize = arr.length.accept(this, env);

		if (typeSize == null || !typeSize.isIntType()) {
			throw new RuntimeException(arr.getLine()+
					": Size of array must be an integer number.");
		}

		// TODO: add array to symbol table
		return new symbolTypes.ArrType(arr.getNodeType());
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
		switch (expr.op) 
		{
			case UMINUS:
				if (type.isIntType()) 
				{
					return new symbolTypes.IntType();
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid type in an unary minus opertaion.");
				}
			case LNEG:
				if (type.isBoolType())
				{
					return new symbolTypes.BoolType();
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

		switch (expr.op) 
		{
			case PLUS:
				if (type1.isIntType() && type2.isIntType()) 
				{
					return new symbolTypes.IntType();
				}
				else if (type1.isStringType() && type2.isStringType())
				{
					return new symbolTypes.StringType();
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
					return new symbolTypes.IntType();
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
					return new symbolTypes.BoolType();
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
						": Invalid  usage of a binary operation <"+ expr.op.toString()+ "> with a non-integer expression.");
				}
			case LAND:
			case LOR:
				if (type1.isBoolType() && type2.isBoolType())
				{
					return new symbolTypes.BoolType();
				}
				else
				{
				throw new RuntimeException(expr.getLine()+
						": Invalid usage of a binary operation <"+ expr.op.toString()+ "> with a non-boolean expression.");
				}
			case NEQUAL:
			case EQUAL:
				if (type1.equals(type2))
					return new symbolTypes.BoolType();
				if ((type1.isNullable()) && (type2.isNullType()) || (type1.isNullType()) && (type2.isNullable()))
					return new symbolTypes.BoolType();
				if ((type1.subTypeOf(type2)) || (type2.subTypeOf(type1)))
					return new symbolTypes.BoolType();
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
		
		for (Stmt s : method.getStatements().statements)
		{
			s.accept(this, env);
		}
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
		
		stmt.getOperation().accept(this, env);

		// Validate "else" statement
		if (stmt.hasElse()) 
		{
			stmt.getElseOperation().accept(this, env);
		}
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
		env.loop_counter += 1;
		stmt.getOperation().accept(this, env);
		env.loop_counter -= 1;
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
		SymbolType varType = lvar.getType().accept(this, env);
		env.symbolTable.InsertNewDecleration(lvar.getName(), varType);
		if(lvar.hasInitValue())
		{
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
		return new BoolType();
	}

	@Override
	public SymbolType visit(ExprLength exprLength, Environment d) {
		SymbolType type1 = exprLength.e.accept(this, d);
		if(!(type1 instanceof ArrType))
		{
			throw new RuntimeException(exprLength.getLine()+
					": Length is only accepted to use as part of an array! and this expression is not an array it is of type " + type1.toString());
		}
		return new IntType();
	}

	@Override
	public SymbolType visit(IntLiteral intLit, Environment d) {
		// TODO Auto-generated method stub
		return new IntType();
	}

	@Override
	public SymbolType visit(newClass nclss, Environment d) {
		SymbolType type1 = d.symbolTable.GetClosestVarWithSameName(nclss.classID);
		if(type1 == null)
		{
			throw new RuntimeException(exprLength.getLine()+
					": there is no class of with the name " + nclss.classID);
		}
		return type1;
	}

	@Override
	public SymbolType visit(NullLiteral nllLit, Environment d) {
		// TODO Auto-generated method stub
		return new NullType();
	}

	@Override
	public SymbolType visit(Return retStmt, Environment d) {
		fixme
		return null;
	}

	@Override
	public SymbolType visit(StaticFunctionCall sfc, Environment d) {
		fixme
		return null;
	}

	@Override
	public SymbolType visit(StringLiteral slit, Environment d) {
		return new StringType();
	}

	@Override
	public SymbolType visit(This thisStmt, Environment d) {
		fixme
		return null;
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
				throw new RuntimeException(varValLoc.getLine()+
						": no variable with the name " + varValLoc.varID + " exists");
			}
			return t1;
		}
	}

	@Override
	public SymbolType visit(VirtualFunctionCall vfc, Environment d) {
		fixme
		return null;
	}

	@Override
	public SymbolType visit(ArrValueLocation arrValLoc, Environment d) {
		fixme
		return null;
	}
}