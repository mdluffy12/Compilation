package slp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class SyntaxAnalyzerAndIRGenerator implements PropagatingVisitor<Environment, symbolTypes.SymbolType> 
{

	protected Program root;
	/** Constructs an SLP interpreter for the given AST.
	 * 
	 * @param root An SLP AST node.
	 */
	public SyntaxAnalyzerAndIRGenerator(ASTNode root) {
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
		
		WriteGeneratedIRCodeToFile(env, "out.lir");
		//System.out.println("Finished Syntax Analysis Successfully");
	}
	
	private void WriteGeneratedIRCodeToFile(Environment env, String sFileName)
	{
		FileWriter writer;
		try {
			writer = new FileWriter(sFileName);
			for(String str: env.GeneratedIRCode) {
				writer.write(str + "\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void InsertLibraryClassInformation(Environment env)
	{
		ClassType libType = new ClassType("Library", null);
		env.symbolTable.InsertNewDeclerationAsBothStaticAndVirtual("Library", libType);
		SymbolType retType;
		SymbolType[] argsTypes;
		MethodType meth1;
		
		retType = new VoidType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new StringType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__println");
		libType.AddNewMember("println", meth1);
		
		retType = new VoidType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new StringType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__print");
		libType.AddNewMember("print", meth1);
		
		retType = new VoidType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new IntType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__printi");
		libType.AddNewMember("printi", meth1);
		
		retType = new VoidType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new BoolType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__printb");
		libType.AddNewMember("printb", meth1);
		
		retType = new IntType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new VoidType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__readi");
		libType.AddNewMember("readi", meth1);
		
		retType = new StringType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new VoidType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__readln");
		libType.AddNewMember("readln", meth1);

		retType = new BoolType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new VoidType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__eof");
		libType.AddNewMember("eof", meth1);

		retType = new IntType();
		argsTypes = new SymbolType[2];
		argsTypes[0] = new StringType();
		argsTypes[1] = new IntType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__stoi");
		libType.AddNewMember("stoi", meth1);

		retType = new StringType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new IntType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__itos");
		libType.AddNewMember("itos", meth1);
		
		retType = new ArrType(new IntType(), 1);
		argsTypes = new SymbolType[1];
		argsTypes[0] = new StringType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__stoa");
		libType.AddNewMember("stoa", meth1);

		retType = new StringType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new ArrType(new IntType(), 1);
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__atos");
		libType.AddNewMember("atos", meth1);
		
		retType = new IntType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new IntType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__random");
		libType.AddNewMember("random", meth1);
		
		retType = new IntType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new VoidType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__time");
		libType.AddNewMember("time", meth1);
		
		retType = new VoidType();
		argsTypes = new SymbolType[1];
		argsTypes[0] = new IntType();
		meth1 = new MethodType(argsTypes, retType, true);
		meth1.SetIRName("__exit");
		libType.AddNewMember("exit", meth1);
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
			List<String> vTable;
			if(clsType.hasSuperClass())
			{
				vTable = new ArrayList<String>(clsType.getSuperClass().GetVirtualTable());
				clsType.iAmountOfVariables = clsType.getSuperClass().iAmountOfVariables; //because it should old all parents vars
			}
			else
			{
				vTable = new ArrayList<String>();
			}
			clsType.SetVirtualTable(vTable);
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
					memberType.SetIRName(""); //class vars does not have names they are used via offset
					memberType.iOffset = clsType.iAmountOfVariables;
					clsType.iAmountOfVariables++;
					clsType.AddNewMember(field.getName(), memberType);
				}
				else
				{
					
					
					ClassMethod met1 = (ClassMethod)member;
					MethodType methodT = SymbolType.createMethType(met1, env.symbolTable);
					memberType = methodT;
					if(met1.name.equals("main"))
					{
						memberType.SetIRName("_ic_main");
					}
					else
					{
						memberType.SetIRName("_" + clsType.getName() + "_" + met1.name);
					}
					if(clsType.GetMemberFromCurrentClassOnly(met1.getName()) != null)
					{
						throw new RuntimeException(member.getLine()+
								":" + "A method with the same name is already defined in this class method overloading or redifining is not allowed");
					}
					SymbolType overriddenMethod = clsType.GetMemberFromMeOrClosestParent(met1.getName());
					
					if(overriddenMethod != null)
					{
						if(overriddenMethod.compareType(memberType) == false)
						{
							throw new RuntimeException(member.getLine()+
									":" + "cannot define the method " + memberType.getName() + " with the type " + memberType.toString() + 
									" as an override to the type " + overriddenMethod.toString() + " the methods doesn't have the same signature or the other is a var");
						}

						if(methodT.getIsStatic() == false)
						{
							//override virtual method
							methodT.iIndexInVirtualTable = ((MethodType)overriddenMethod).iIndexInVirtualTable;
							vTable.set(methodT.iIndexInVirtualTable, methodT.GetIRName());
						}
						
					}
					else
					{
						if(methodT.getIsStatic() == false)
						{
							methodT.iIndexInVirtualTable = vTable.size();
							vTable.add(methodT.GetIRName());
						}
					}
					clsType.AddNewMember(met1.getName(), memberType);
				}
			}
			env.GeneratedIRCode.add("_VT_" + clsType.getName() + ":" + vTable.toString());
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
		
		SymbolType type2 = stmt.rhs.accept(this, env);
		if(type2 == null)
		{
			throw new RuntimeException(stmt.getLine()+
					": can not perform assignment from type void");
		}
		
		stmt.varExpr.bIsLeftSideOfAssignStmt = true; //set it so it won't create a temperoray and the name will be the true var name
		 //where we should store the value
		stmt.varExpr.sAssignIRName = type2.GetIRName();
		SymbolType type1 = stmt.varExpr.accept(this, env);
		
		
		if (type1 == null) 
		{
			throw new RuntimeException(stmt.getLine()+
					": can not perform assignment to type void.");
		}
		
		if(!type2.subTypeOf(type1))
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

		SymbolType t1 = arr.type.accept(this, env);
		ArrType retType;
		if(t1 instanceof ArrType)
		{
			ArrType art1 = (ArrType)t1;
			retType =  new ArrType(art1.getElemType(), art1.getDimension() + 1);
		}
		else
		{
			retType =  new ArrType(t1, 1);
		}
		retType.SetIRName("R"+env.iCurrentAvailableTempNumber);
		env.iCurrentAvailableTempNumber++;
		env.GeneratedIRCode.add("Mul 4, " + typeSize.GetIRName());
		env.GeneratedIRCode.add("Library __allocateArray(" + typeSize.GetIRName() + "), " + retType.GetIRName());
		return retType;
	}

	public void GenerateLogicalNotCode(String sRegisterName, Environment env)
	{
		env.GeneratedIRCode.add("#logical not");
		env.GeneratedIRCode.add("Not " + sRegisterName);
		env.GeneratedIRCode.add("Compare 0, " + sRegisterName); //the bool for true is 1 32 times otherwise we have a problem
		int iLabelNumber = env.GlobalLabelCounter;
		env.GlobalLabelCounter++;
		String sLabelName = "_Label_Logical_Not_" + env.currentMethod.GetIRName() + "_" + iLabelNumber;
		env.GeneratedIRCode.add("JumpTrue " + sLabelName);
		env.GeneratedIRCode.add("Move 1, " + sRegisterName);
		env.GeneratedIRCode.add(sLabelName + ":");
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
					env.GeneratedIRCode.add("Neg " + type.GetIRName());
					SymbolType newType = new symbolTypes.IntType();
					newType.SetIRName(type.GetIRName());
					return newType;
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid type in an unary minus opertaion.");
				}
			case LNEG:
				if (type.isBoolType())
				{
					
					GenerateLogicalNotCode(type.GetIRName(), env);
					SymbolType newType = new symbolTypes.BoolType();
					newType.SetIRName(type.GetIRName());
					return newType;
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
					env.GeneratedIRCode.add("Add "+type1.GetIRName()+" ,"+type2.GetIRName());
					SymbolType newType = new symbolTypes.IntType();
					newType.SetIRName(type2.GetIRName());
					return newType;
				}
				else if (type1.isStringType() && type2.isStringType())
				{ //need to change it
					
					SymbolType newType = new symbolTypes.StringType();
					newType.SetIRName("R"+env.iCurrentAvailableTempNumber);
					env.iCurrentAvailableTempNumber++;
					env.GeneratedIRCode.add("Library __stringCat(" +type1.GetIRName()+","+type2.GetIRName()+") ," + newType.GetIRName());
					return newType;
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid  usage of a arithmetic operation <" + expr.op.toString() +">" );
				}
			case MINUS:
				if (type1.isIntType() && type2.isIntType()) {
					env.GeneratedIRCode.add("Sub "+ type2.GetIRName()+" ,"+ type1.GetIRName());
					SymbolType newType = new symbolTypes.IntType();
					newType.SetIRName(type1.GetIRName());
					return newType;
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid  usage of a arithmetic operation <" + expr.op.toString() +">." );
				}
			case MULT:
				if (type1.isIntType() && type2.isIntType()) {
					env.GeneratedIRCode.add("Mul "+type1.GetIRName()+" ,"+type2.GetIRName());
					SymbolType newType = new symbolTypes.IntType();
					newType.SetIRName(type2.GetIRName());
					return newType;
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid  usage of a arithmetic operation <" + expr.op.toString() +">." );
				}
			case DIV:
				if (type1.isIntType() && type2.isIntType()) {
					env.GeneratedIRCode.add("Div "+type2.GetIRName()+" ,"+type1.GetIRName());
					SymbolType newType = new symbolTypes.IntType();
					newType.SetIRName(type1.GetIRName());
					return newType;
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid  usage of a arithmetic operation <" + expr.op.toString() +">." );
				}
			case MOD:
				if (type1.isIntType() && type2.isIntType()) {
					env.GeneratedIRCode.add("Mod "+type2.GetIRName()+" ,"+type1.GetIRName());
					SymbolType newType = new symbolTypes.IntType();
					newType.SetIRName(type1.GetIRName());
					return newType;
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid  usage of a arithmetic operation <" + expr.op.toString() +">." );
				}
			case GT:
				SymbolType tmp = type1;
				type1 = type2;
				type2 = tmp;
				env.GeneratedIRCode.add("#a > b");
			case LT:
				env.GeneratedIRCode.add("#a < b");
				if (type1.isIntType() && type2.isIntType())
				{
					env.GeneratedIRCode.add("Sub " + type2.GetIRName() + ", " + type1.GetIRName());
					env.GeneratedIRCode.add("Not " + type1.GetIRName());
					env.GeneratedIRCode.add("Or 2147483647, " + type1.GetIRName());
					env.GeneratedIRCode.add("Not " + type1.GetIRName());
					SymbolType newType = new symbolTypes.BoolType();
					newType.SetIRName(type1.GetIRName());
					return newType;
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
						": Invalid  usage of a binary operation '"+ expr.op.toString()+
						"' with a non-integer expression. expressions are left: " + type1.toString() + " right: " + type2.toString());
				}
				
			case LE:
				tmp = type1;
				type1 = type2;
				type2 = tmp;
				env.GeneratedIRCode.add("#a <= b");
			case GE: //need to change it not good
				env.GeneratedIRCode.add("#a >= b");
				if (type1.isIntType() && type2.isIntType())
				{
					env.GeneratedIRCode.add("Sub " + type2.GetIRName() + ", " + type1.GetIRName());
					env.GeneratedIRCode.add("Or 2147483647, " + type1.GetIRName()); 
					env.GeneratedIRCode.add("Not " + type1.GetIRName()); 
					SymbolType newType = new symbolTypes.BoolType();
					newType.SetIRName(type1.GetIRName());
					return newType;
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
						": Invalid  usage of a binary operation '"+ expr.op.toString()+
						"' with a non-integer expression. expressions are left: " + type1.toString() + " right: " + type2.toString());
				}
			case LAND:
				if (type1.isBoolType() && type2.isBoolType())
				{
					env.GeneratedIRCode.add("And "+type1.GetIRName()+" ,"+type2.GetIRName());
					SymbolType newType = new symbolTypes.BoolType();
					newType.SetIRName(type2.GetIRName());
					return newType;
				}
				else
				{
				throw new RuntimeException(expr.getLine()+
						": Invalid usage of a binary operation <"+ expr.op.toString()+ "> with a non-boolean expression.");
				}
			case LOR:
				if (type1.isBoolType() && type2.isBoolType())
				{
					env.GeneratedIRCode.add("Or "+type1.GetIRName()+" ,"+type2.GetIRName());
					SymbolType newType = new symbolTypes.BoolType();
					newType.SetIRName(type2.GetIRName());
					return newType;
				}
				else
				{
				throw new RuntimeException(expr.getLine()+
						": Invalid usage of a binary operation <"+ expr.op.toString()+ "> with a non-boolean expression.");
				}
			case NEQUAL:
			case EQUAL://neeed to fix it
				boolean bIsLegal = false;;
				bIsLegal = bIsLegal || type1.equals(type2);
				bIsLegal = bIsLegal || (type1.isNullable()) && (type2.isNullType()) || (type1.isNullType()) && (type2.isNullable());
				bIsLegal = bIsLegal || (type1.subTypeOf(type2)) || (type2.subTypeOf(type1));
				if(false == bIsLegal)
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid usage of a binary operation <"+ expr.op.toString()+ "> with a mismatching expression.");
				}
				else
				{
					SymbolType newType = new BoolType();
					newType.SetIRName(type2.GetIRName());
					env.GeneratedIRCode.add("Xor " + type1.GetIRName() + " ," + type2.GetIRName());
					if(expr.op == slp.Operator.EQUAL)
					{
						GenerateLogicalNotCode(newType.GetIRName(), env);
					}
					return newType;
				}
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
		env.currentClass = classType;
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
			formalType.SetIRName(formal.getName());
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
		env.currentMethod = SymbolType.createMethType(method, env.symbolTable);
		env.currentMethod.SetIRName(env.currentClass.GetMemberFromCurrentClassOnly(method.name).GetIRName());
		env.GeneratedIRCode.add(env.currentMethod.GetIRName() + ":");
		env.LocalVarIndex = 0; //so vars will start from 0
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
		if(env.has_return_in_every_path == false)
		{
			//add implicit void return 
			if(env.currentMethod.GetIRName().equals("_ic_main"))
			{
				env.GeneratedIRCode.add("Library __exit(0), Rdummy");
			}
			else
			{
				env.GeneratedIRCode.add("Return 9999");
			}
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
		env.GeneratedIRCode.add("#line " + call.getLine());
		call.getCall().accept(this, env);
		return null;
	}
	
	public SymbolType visit(If stmt, Environment env) 
	{
		env.GeneratedIRCode.add("#line " + stmt.getLine());
		int iLabelNumber = env.GlobalLabelCounter;
		env.GlobalLabelCounter++;
		String sLabelName = "_NotEnteringIfLabel_" + env.currentMethod.GetIRName() + "_" + iLabelNumber;
		SymbolType sym_type = stmt.getCondition().accept(this, env);
		if(!sym_type.isBoolType())
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid expression inside if, condition is not of boolean type.");
		}
		
		env.GeneratedIRCode.add("Compare 0," + sym_type.GetIRName());
		env.GeneratedIRCode.add("JumpTrue " + sLabelName);
		boolean has_return_before = env.has_return_in_every_path;
		env.has_return_in_every_path = false;
		stmt.getOperation().accept(this, env);
		boolean has_return_in_if = env.has_return_in_every_path;
		boolean has_return_in_else = false;
		String sEndIfLabel = "_EndOfIfLabel_" + env.currentMethod.GetIRName() + "_" + iLabelNumber;
		if (stmt.hasElse()) 
		{
			env.GeneratedIRCode.add("Jump " + sEndIfLabel);
		}
		env.GeneratedIRCode.add(sLabelName + ":");
		
		// Validate "else" statement
		if (stmt.hasElse()) 
		{
			env.has_return_in_every_path = false;
			stmt.getElseOperation().accept(this, env);
			has_return_in_else = env.has_return_in_every_path;
			env.GeneratedIRCode.add(sEndIfLabel + ":");
		}
		
		
		env.has_return_in_every_path = (has_return_before) || (has_return_in_if && has_return_in_else);
		return null;
	}
	
	public SymbolType visit(While stmt, Environment env) 
	{
		env.GeneratedIRCode.add("#line " + stmt.getLine());
		int iLabelNumber = env.GlobalLabelCounter;
		env.GlobalLabelCounter++;
		String sLabelName = "_StartWhile_" + env.currentMethod.GetIRName() + "_" + iLabelNumber;
		env.CurrentWhilesStartLabel.add(sLabelName);
		env.GeneratedIRCode.add(sLabelName + ":");
		SymbolType sym_type = stmt.getCondition().accept(this,env);
		if(!sym_type.isBoolType())
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid expression inside While, condition is not of boolean type.");
		}
		
		String sEndLabelName = "_EndWhile_" + env.currentMethod.GetIRName() + "_" + iLabelNumber;
		env.CurrentWhilesExitLabel.add(sEndLabelName);
		
		env.GeneratedIRCode.add("Compare 0," + sym_type.GetIRName());
		env.GeneratedIRCode.add("JumpTrue " + sEndLabelName);
		
		boolean has_return_before = env.has_return_in_every_path;
		env.loop_counter += 1;
		stmt.getOperation().accept(this, env);
		env.loop_counter -= 1;
		env.has_return_in_every_path = has_return_before;
		
		env.GeneratedIRCode.add("Jump " + sLabelName);
		env.GeneratedIRCode.add(sEndLabelName + ":");
		env.CurrentWhilesStartLabel.remove(env.CurrentWhilesStartLabel.size()-1);
		env.CurrentWhilesExitLabel.remove(env.CurrentWhilesExitLabel.size() -1);
		return null;
	}
	
	public SymbolType visit(Break stmt, Environment env)
	{
		env.GeneratedIRCode.add("#line(BREAK) " + stmt.getLine());
		if (env.loop_counter == 0)
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid  usage of a break statement not inside of loop");
		}
		env.GeneratedIRCode.add("Jump " + env.CurrentWhilesExitLabel.get(env.CurrentWhilesExitLabel.size() - 1));
		return null;
	}

	public SymbolType visit(Continue stmt,  Environment env)
	{
		env.GeneratedIRCode.add("#line " + stmt.getLine());
		if (env.loop_counter == 0)
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid  usage of a continue statement not inside of loop");
		}
		env.GeneratedIRCode.add("Jump " + env.CurrentWhilesStartLabel.get(env.CurrentWhilesStartLabel.size() - 1));
		return null;
	}
	
	@Override
	public SymbolType visit(PrimitiveType ptype, Environment d) {
		return SymbolType.getTypeFromAST(ptype, d.symbolTable).Clone();
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
		return t1.Clone();
	}

	@Override
	public SymbolType visit(LocalVar lvar, Environment env) {
		env.GeneratedIRCode.add("#line " + lvar.getLine());
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
		varType.SetIRName("local_var_" + lvar.getName() + "_id_" + env.LocalVarIndex);
		env.LocalVarIndex++;
		env.symbolTable.InsertNewDecleration(lvar.getName(), varType);
		if(lvar.hasInitValue())
		{
			SymbolType initValType = lvar.getInitValue().accept(this, env);
			if(initValType.subTypeOf(varType) == false)
			{
				throw new RuntimeException(lvar.getLine()+
						": it is not allowed to initialize a var with type " + varType.toString() + " with type " + initValType.toString() + " which is not a subtype");
			}
			env.GeneratedIRCode.add("Move " + initValType.GetIRName() + ", " + varType.GetIRName());
		}
		return null;
	}

	@Override
	public SymbolType visit(BoolLiteral bl, Environment d) {
		SymbolType type1 = new BoolType();
		String sIRName = "R" + d.iCurrentAvailableTempNumber;
		d.iCurrentAvailableTempNumber++;
		type1.SetIRName(sIRName);
		if(bl.value == true)
		{
			d.GeneratedIRCode.add("Move 1 ," + sIRName);
		}
		else
		{
			d.GeneratedIRCode.add("Move 0 ," + sIRName);
		}
		return type1;
	}

	@Override
	public SymbolType visit(ExprLength exprLength, Environment d) {
		SymbolType type1 = exprLength.e.accept(this, d);
		if(!(type1 instanceof ArrType))
		{
			throw new RuntimeException(exprLength.getLine()+
					": Length is only accepted to use as part of an array! and this expression is not an array it is of type " + type1.toString());
		}
		SymbolType typeLength = new IntType();
		String sIRName = "R" + d.iCurrentAvailableTempNumber;
		d.iCurrentAvailableTempNumber++;
		typeLength.SetIRName(sIRName);
		d.GeneratedIRCode.add("ArrayLength " + type1.GetIRName() + ", " + sIRName);
		
		return typeLength;
	}

	@Override
	public SymbolType visit(IntLiteral intLit, Environment d) {
		SymbolType type1 = new IntType();
		String sIRName = "R" + d.iCurrentAvailableTempNumber;
		d.iCurrentAvailableTempNumber++;
		type1.SetIRName(sIRName);
		d.GeneratedIRCode.add("Move " + intLit.value + " ," + sIRName);
		return type1;
	}

	@Override
	public SymbolType visit(newClass nclss, Environment d) {
		SymbolType type1 = d.symbolTable.GetClosestVarWithSameName(nclss.classID);
		if(type1 == null)
		{
			throw new RuntimeException(nclss.getLine()+
					": there is no class of with the name " + nclss.classID);
		}
		
		ClassType clsType1 = (ClassType) type1;
		int iAmountOfMemoryInBytes = (clsType1.iAmountOfVariables)*4;
		String sIRName = "R" + d.iCurrentAvailableTempNumber;
		d.iCurrentAvailableTempNumber++;
		SymbolType retT = type1.Clone();
		retT.SetIRName(sIRName);
		d.GeneratedIRCode.add("Library __allocateObject(" + iAmountOfMemoryInBytes + "), " + sIRName);
		d.GeneratedIRCode.add("MoveField _VT_" + nclss.classID + ", " + sIRName + ".0");
		return retT;
	}

	@Override
	public SymbolType visit(NullLiteral nllLit, Environment d) {
		SymbolType type1 = new NullType();
		String sIRName = "R" + d.iCurrentAvailableTempNumber;
		d.iCurrentAvailableTempNumber++;
		type1.SetIRName(sIRName);
		d.GeneratedIRCode.add("Move 0 ," + sIRName); //define NULL as 0!
		return type1;
	}

	@Override
	public SymbolType visit(Return retStmt, Environment d) {
		d.GeneratedIRCode.add("#line " + retStmt.getLine());
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
			d.GeneratedIRCode.add("Return " + returnExprType.GetIRName());
			return returnExprType;
		}
		if(d.currentMethod.GetIRName().equals("_ic_main"))
		{
			d.GeneratedIRCode.add("Library __exit(0), Rdummy");
		}
		else
		{
			d.GeneratedIRCode.add("Return 9999");
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
		MethodType ftype2 = new MethodType(params, ftype1.getRetType().Clone(), true);
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
		
		
		//generate static call lir code
		String sCommand;
		
		if(sfc.classID.equals("Library"))
		{
			sCommand = "Library ";
		}
		else
		{
			sCommand = "StaticCall ";
		}
		
		sCommand += ftype1.GetIRName() + "(";
		
		for(int i = 0; i < ftype1.getArgs().length; i++)
		{
			if(ftype1.getArgs()[i].isVoidType())
			{
				break;
			}
			if(i != 0)
			{
				sCommand += ", ";
			}
			if(sfc.classID.equals("Library") == false)
			{
				sCommand += ftype1.getArgs()[i].GetIRName() + "=";
			}
			sCommand += ftype2.getArgs()[i].GetIRName();
		}
		
		sCommand += "), ";
		if(ftype1.getRetType().isVoidType())
		{
			sCommand += "Rdummy";
		}
		else
		{
			String sIRName = "R" + d.iCurrentAvailableTempNumber;
			d.iCurrentAvailableTempNumber++;
			ftype1.getRetType().SetIRName(sIRName);
			sCommand += sIRName;
		}
		
		d.GeneratedIRCode.add(sCommand);
		
		return ftype1.getRetType().Clone();
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
			else
			{
				t1 = t1.Clone();
				String sIRName = "R" + d.iCurrentAvailableTempNumber;
				d.iCurrentAvailableTempNumber++;
				d.GeneratedIRCode.add("Move this, " + sIRName);
				t1.SetIRName(sIRName);
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
		MethodType ftype2 = new MethodType(params, ftype1.getRetType().Clone(), ftype1.getIsStatic());
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
		
		if(ftype1.getIsStatic())
		{
			//generate static call lir code
			String sCommand;
			sCommand = "StaticCall ";
			
			sCommand += ftype1.GetIRName() + "(";
			
			for(int i = 0; i < ftype1.getArgs().length; i++)
			{
				if(ftype1.getArgs()[i].isVoidType())
				{
					break;
				}
				if(i != 0)
				{
					sCommand += ", ";
				}
				sCommand += ftype1.getArgs()[i].GetIRName() + "=";
				sCommand += ftype2.getArgs()[i].GetIRName();
			}
			
			sCommand += "), ";
			if(ftype1.getRetType().isVoidType())
			{
				sCommand += "Rdummy";
			}
			else
			{
				String sIRName = "R" + d.iCurrentAvailableTempNumber;
				d.iCurrentAvailableTempNumber++;
				ftype1.getRetType().SetIRName(sIRName);
				sCommand += sIRName;
			}
			
			d.GeneratedIRCode.add(sCommand);
		}
		else
		{
			//generate virtual call lir code
			String sIRName = "R" + d.iCurrentAvailableTempNumber;
			d.iCurrentAvailableTempNumber++;
			d.GeneratedIRCode.add("Move " + clssT1.GetIRName() + ", " + sIRName);
			String sCommand = "VirtualCall " + sIRName + "." + ftype1.iIndexInVirtualTable + "(";
			
			for(int i = 0; i < ftype1.getArgs().length; i++)
			{
				if(ftype1.getArgs()[i].isVoidType())
				{
					break;
				}
				if(i != 0)
				{
					sCommand += ", ";
				}
				sCommand += ftype1.getArgs()[i].GetIRName() + "=" + ftype2.getArgs()[i].GetIRName();
			}
			
			sCommand += "), ";
			if(ftype1.getRetType().isVoidType())
			{
				sCommand += "Rdummy";
			}
			else
			{
				String sIRName2 = "R" + d.iCurrentAvailableTempNumber;
				d.iCurrentAvailableTempNumber++;
				ftype1.getRetType().SetIRName(sIRName2);
				sCommand += sIRName2;
			}
			
			d.GeneratedIRCode.add(sCommand);
		}
		
		return ftype1.getRetType().Clone();
	}
	
	@Override
	public SymbolType visit(StringLiteral slit, Environment d) {
		String sStrName = "string_literal_" + d.GlobalStringCounter;
		d.GeneratedIRCode.add(0, sStrName + ": " + slit.value);
		d.GlobalStringCounter++;
		SymbolType t1 = new StringType();
		String sIRName = "R" + d.iCurrentAvailableTempNumber;
		d.iCurrentAvailableTempNumber++;
		t1.SetIRName(sIRName);
		d.GeneratedIRCode.add("Move " + sStrName + " ," + sIRName);
		return t1;
	}

	@Override
	public SymbolType visit(This thisStmt, Environment d) {
		SymbolType thisType = d.symbolTable.GetClosestVarWithSameName("This");
		if(thisType == null)
		{
			throw new RuntimeException(thisStmt.getLine()+
					": 'this' could not been found at this context make sure you are inside virtual method of a class");
		}
		thisType = thisType.Clone();
		String sIRName = "R" + d.iCurrentAvailableTempNumber;
		d.iCurrentAvailableTempNumber++;
		thisType.SetIRName(sIRName);
		d.GeneratedIRCode.add("Move this, " + sIRName);
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
			SymbolType retT = t1.Clone();
			if(varValLoc.bIsLeftSideOfAssignStmt)
			{
				d.GeneratedIRCode.add("MoveField " + varValLoc.sAssignIRName + ", " + t2.GetIRName() + "." + t1.iOffset);
			}
			else
			{
				String sIRName = "R" + d.iCurrentAvailableTempNumber;
				d.iCurrentAvailableTempNumber++;
				d.GeneratedIRCode.add("MoveField " + t2.GetIRName() + "." + retT.iOffset + ", " + sIRName);
				retT.SetIRName(sIRName);
			}
			return retT;
		}
		else
		{
			t1 = d.symbolTable.GetClosestVarWithSameName(varValLoc.varID);
			if(t1 == null || t1.iOffset >= 0) //that means it is implicit this cause its a class var
			{
				ClassType clsThis = (ClassType) d.symbolTable.GetClosestVarWithSameName("This"); //check maybe it is implicit this var
				if(clsThis != null)
				{
					t1 = clsThis.GetMemberFromMeOrClosestParent(varValLoc.varID);
					if(t1 != null)
					{
						SymbolType retT = t1.Clone();
						if(retT.iOffset < 0)
						{
							throw new RuntimeException(varValLoc.getLine()+
									": variable of This class offset is not valid for some reason {" + varValLoc.varID + "}");
						}
						if(varValLoc.bIsLeftSideOfAssignStmt)
						{
							String sIRName = "R" + d.iCurrentAvailableTempNumber;
							d.iCurrentAvailableTempNumber++;
							d.GeneratedIRCode.add("Move this, " + sIRName);
							d.GeneratedIRCode.add("MoveField " + varValLoc.sAssignIRName + ", " + sIRName + "." + retT.iOffset);
						}
						else
						{
							String sIRName = "R" + d.iCurrentAvailableTempNumber;
							d.iCurrentAvailableTempNumber++;
							d.GeneratedIRCode.add("Move this, " + sIRName);
							String sIRName2 = "R" + d.iCurrentAvailableTempNumber;
							d.iCurrentAvailableTempNumber++;
							d.GeneratedIRCode.add("MoveField " + sIRName + "." + retT.iOffset + ", " + sIRName2);
							retT.SetIRName(sIRName2);
						}
						return retT;
					}
					
				}
				throw new RuntimeException(varValLoc.getLine()+
						": no variable with the name " + varValLoc.varID + " exists");
			}
			SymbolType retT = t1.Clone();
			//just a simple local variable (or parameter which is the same)
			if(varValLoc.bIsLeftSideOfAssignStmt)
			{
				d.GeneratedIRCode.add("Move " + varValLoc.sAssignIRName + ", " + retT.GetIRName());
			}
			else
			{
				String sIRName = "R" + d.iCurrentAvailableTempNumber;
				d.iCurrentAvailableTempNumber++;
				d.GeneratedIRCode.add("Move " + retT.GetIRName() + ", " + sIRName);
				retT.SetIRName(sIRName);
			}
			return retT;
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
		SymbolType retType;
		if(arrT.getDimension() == 1) //it is the last dimension we should return a not array type
		{
			retType =  arrT.getElemType().Clone();
		}
		else
		{
			retType = new ArrType(arrT.getElemType(), arrT.getDimension() - 1);
		}
		if(arrValLoc.bIsLeftSideOfAssignStmt)
		{
			d.GeneratedIRCode.add("MoveArray " + arrValLoc.sAssignIRName + ", " + arrT.GetIRName() + "[" + indType.GetIRName() + "]");
		}
		else
		{
			String sIRName = "R" + d.iCurrentAvailableTempNumber;
			d.iCurrentAvailableTempNumber++;
			d.GeneratedIRCode.add("MoveArray " + arrT.GetIRName() + "[" + indType.GetIRName() + "]" + ", " + sIRName);
			retType.SetIRName(sIRName);
		}
		return retType;
	}
}