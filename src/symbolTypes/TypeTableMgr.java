package symbolTypes;

import slp.*;

public class TypeTableMgr implements Visitor
{
	private TypeTable typeTable;
	private int lineNum;
	private String errorStr;
	private boolean visitErr = false;;

	public TypeTableMgr() 
	{
	}

	public TypeTable getTypeTable() 
	{
		return this.typeTable;
	}

	public void create(Program program)
	{
		this.typeTable = new TypeTable();
				
		if (!containsMain(program))
		{
			throw new RuntimeException(lineNum+":"+ errorStr);
		}
		
		program.accept(this);
		if (visitErr == true)
		{
			throw new RuntimeException(lineNum+":"+ errorStr);
		}
	}

	private Boolean containsMain(Program program) {
		int mainCount = 0;
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
						this.lineNum = method.getLine();
					}
				}
			}
		}
		if (mainCount == 0) 
		{
			this.errorStr = "Main method is undeclared.";
			return false;
		}
		if (mainCount != 1)
		{
			this.errorStr = "Main method declared multiple times.";
			return false;
		}
		if (!mainMethod.getType().getName().equals("void")){
			this.errorStr = "Return type for Main method must be 'void'.";
			return false;
		}

		for (MethodFormal formal : mainMethod.getFormals())
		{
			formal.accept(this);
		}
		
		mainMethod.getType().accept(this);

		typeTable.addMethodTypeToDict(mainMethod);
		return true;
	}

	private void genVisitType(slp.Type type) 
	{
		if (type.getDimension() > 0){
			typeTable.addArrayTypeToDict(type);
		}
	}
	
	private void genVisitMethod(ClassMethod method) 
	{
		for (MethodFormal formal : method.getFormals())
		{
			if (visitErr)
			{
				return;
			}
			formal.accept(this);
		}
		method.getType().accept(this);
		typeTable.addMethodTypeToDict(method);
		for (Stmt stmnt : method.getStatements().statements)
		{
			if (visitErr)
			{
				return;
			}
			stmnt.accept(this);
		}
	}
	
	@Override
	public void visit(Program program) 
	{
		for (ICClass cls : program.getClasses())
		{
			if (visitErr)
			{
				return;
			}
			 cls.accept(this);
		}
	}

	@Override
	public void visit(ICClass cls) 
	{
		if (!typeTable.addClassTypeToDict(cls)) 
		{
			this.lineNum = cls.getLine();
			this.errorStr = "Can not extended an undeclared class: " + cls.getSuperClassName();
			visitErr = true;
			return;
		}
		for (ClassMember member : cls.getMembers())
		{
			if (visitErr)
			{
				return;
			}
			member.accept(this);
		}
	}

	@Override
	public void visit(ClassField field) 
	{
		field.getType().accept(this);
	}

	@Override
	public void visit(VirtualMethod method) 
	{
		genVisitMethod(method);
	}

	@Override
	public void visit(StaticMethod method) 
	{
		genVisitMethod(method);
	}


	@Override
	public void visit(MethodFormal formal) 
	{
		formal.getType().accept(this);
	}

	@Override
	public void visit(PrimitiveType type) 
	{
		genVisitType(type);
	}

	@Override
	public void visit(ObjectClassType type) 
	{
		genVisitType(type);
	}

	@Override
	public void visit(AssignStmt assignment) 
	{
	}

	@Override
	public void visit(CallStmt callStatement) 
	{
	}

	@Override
	public void visit(Return returnStatement) 
	{

	}

	@Override
	public void visit(If ifStmt) 
	{
		ifStmt.getOperation().accept(this);
		
		if (ifStmt.hasElse())
		{
			ifStmt.getElseOperation().accept(this);
		}
	}

	@Override
	public void visit(While whileStmt) 
	{
		whileStmt.getOperation().accept(this);
	}

	@Override
	public void visit(Break breakStmt) 
	{
	}

	@Override
	public void visit(Continue continueStmt) 
	{
	}

	@Override
	public void visit(BlockStmt blk) {
		for (Stmt stmnt : blk.m_stmtList.statements)
		{
			if (visitErr)
			{
				return;
			}
			stmnt.accept(this);
		}
	}

	@Override
	public void visit(LocalVar var) 
	{
		var.getType().accept(this);
	}

	@Override
	public void visit(StaticFunctionCall call) 
	{
	}

	@Override
	public void visit(VirtualFunctionCall call) 
	{
	}

	@Override
	public void visit(This expr) 
	{
	}

	@Override
	public void visit(newClass newCls) 
	{
	}

	@Override
	public void visit(newArray arr) 
	{
		arr.type.accept(this);
	}

	@Override
	public void visit(BinaryOpExpr expr) 
	{
	}

	@Override
	public void visit(UnaryOpExpr expr) 
	{
	}

	@Override
	public void visit(StmtList stmts) 
	{
	}

	@Override
	public void visit(ExprLength eLength) 
	{
	}

	@Override
	public void visit(NullLiteral lit) 
	{
	}

	@Override
	public void visit(IntLiteral lit) 
	{
	}

	@Override
	public void visit(StringLiteral lit) 
	{	
	}

	@Override
	public void visit(BoolLiteral lit) 
	{
	}

	@Override
	public void visit(VarValueLocation expr) 
	{
	}

	@Override
	public void visit(ArrValueLocation expr) 
	{
	}

	@Override
	public void visit(ArgumentList lst) 
	{	
	}



}
