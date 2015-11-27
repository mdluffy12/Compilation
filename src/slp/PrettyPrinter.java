package slp;

/** Pretty-prints an SLP AST.
 */
public class PrettyPrinter implements Visitor {
	protected final ASTNode root;
	protected int tabsAmount;
	/** Constructs a printin visitor from an AST.
	 * 
	 * @param root The root of the AST.
	 */
	public PrettyPrinter(ASTNode root) {
		this.root = root;
	}

	/** Prints the AST with the given root.
	 */
	public void print() {
		tabsAmount = 0;
		root.accept(this);
	}
	
	protected void PrintTabs()
	{
		for(int i = 0; i < tabsAmount; i++)
		{
			System.out.print("\t");
		}
	}
	
	public void visit(Program p1) {
		PrintTabs();
		System.out.println("Program");
		tabsAmount ++;
		for (ICClass c1 : p1.getClasses())
		{
			c1.accept(this);	
		}
		tabsAmount--;
	}
	
	public void visit(ICClass c1) {
		PrintTabs();
		System.out.print("Class " + c1.getName());
		if(c1.hasSuperClass())
		{
			System.out.print(": " + c1.getSuperClassName());
		}
		System.out.println("");
		tabsAmount ++;
		for (ClassMember cm1 : c1.getMembers())
		{
			cm1.accept(this);
		}
		tabsAmount--;
	}
	
	public void visit(StaticMethod sm1) {
		PrintTabs();
		System.out.print("Static");
		this.visitMethod(sm1);
	}
	
	public void visit(VirtualMethod vm1) {
		PrintTabs();
		System.out.print("Virtual");
		this.visitMethod(vm1);
	}
	
	protected void visitMethod(ClassMethod sm1) {
		System.out.println(" Method " + sm1.getName());
		
		tabsAmount++;
		
		//Return Type
		PrintTabs();
		System.out.println("Return Type");
		tabsAmount++;
		sm1.getType().accept(this);
		tabsAmount--;
		
		PrintTabs();
		System.out.println("Parameters");
		tabsAmount++;
		for (MethodFormal mf : sm1.getFormals())
		{
			mf.accept(this);
		}
		tabsAmount--;
		
		PrintTabs();
		System.out.println("Statements");
		tabsAmount++;
		sm1.getStatements().accept(this);
		tabsAmount--;
		
		tabsAmount--;
	}
	
	public void visit(ObjectClassType octype)
	{
		PrintTabs();
		System.out.println("Type " + octype.getName() + " with dimension " + octype.getDimension());
	}
	
	public void visit(PrimitiveType pt1)
	{
		PrintTabs();
		System.out.println("Type " + pt1.getName() + " with dimension " + pt1.getDimension());
	}
	
	public void visit(MethodFormal formal1)
	{
		PrintTabs();
		System.out.println("Type " + formal1.getType().getName() + " with dimension " + formal1.getType().getDimension() + ", Name " + formal1.getName());
	}
	public void visit(StmtList stList1)
	{
		for(Stmt st1 : stList1.statements)
		{
			st1.accept(this);
		}
	}
	
	public void visit(ClassField field1)
	{
		PrintTabs();
		System.out.println("Class Field - Type " + field1.getType().getName() + " with dimension " + field1.getType().getDimension() + ", Name " + field1.getName());
	}
	
	public void visit(AssignStmt st1)
	{
		PrintTabs();
		System.out.println("Assign Statement (ValueLocation, RightExpression)");
		tabsAmount++;
		st1.varExpr.accept(this);
		st1.rhs.accept(this);
		tabsAmount--;
	}
	
	public void visit(LocalVar lv1)
	{
		PrintTabs();
		System.out.println("Local Var Decleration - Type " + lv1.getType().getName() + " with dimension " + lv1.getType().getDimension() + ", Name " + lv1.getName());
		tabsAmount++;
		if(lv1.hasInitValue())
		{
			//Return Type
			PrintTabs();
			System.out.println("Init Value Expression");
			tabsAmount++;
			lv1.getInitValue().accept(this);
			tabsAmount--;
		}
		tabsAmount--;
	}
	
	public void visit(VarValueLocation expr)
	{
		PrintTabs();
		System.out.println("Left Var Location - ID " + expr.varID);
		tabsAmount++;
		if(expr.UseVarExpr)
		{
			PrintTabs();
			System.out.println("Dot Expression Prefix");
			tabsAmount++;
			expr.varExpression.accept(this);
			tabsAmount--;
		}
		
		tabsAmount--;
	}
	
	public void visit(ArrValueLocation expr)
	{
		PrintTabs();
		System.out.println("Array Left Value Location");
		tabsAmount++;
		
		PrintTabs();
		System.out.println("Base Expression");
		tabsAmount++;
		expr.baseExpression.accept(this);
		tabsAmount--;
		
		PrintTabs();
		System.out.println("Index Expression");
		tabsAmount++;
		expr.indexExpression.accept(this);
		tabsAmount--;
		
		tabsAmount--;
	}
	
	public void visit(While stmt)
	{
		PrintTabs();
		System.out.println("While Statement");
		tabsAmount++;
		
		PrintTabs();
		System.out.println("Condition");
		tabsAmount++;
		stmt.getCondition().accept(this);
		tabsAmount--;
		
		PrintTabs();
		System.out.println("Operation");
		tabsAmount++;
		stmt.getOperation().accept(this);
		tabsAmount--;
		
		tabsAmount--;
	}
	
	public void visit(NullLiteral lit)
	{
		PrintTabs();
		System.out.println("Null Literal");
	}
	
	public void visit(IntLiteral lit)
	{
		PrintTabs();
		System.out.println("Int Literal: " + lit.value);
	}
	
	public void visit(StringLiteral lit)
	{
		PrintTabs();
		System.out.println("String Literal: " + lit.value);
	}
	
	public void visit(BoolLiteral lit)
	{
		PrintTabs();
		System.out.println("Bool Literal: " + lit.value);
	}
	
	public void visit(BlockStmt stmt)
	{
		PrintTabs();
		System.out.println("Block Statement");
		tabsAmount++;
		stmt.m_stmtList.accept(this);
		tabsAmount--;
	}
	
	public void visit(BinaryOpExpr expr)
	{
		PrintTabs();
		System.out.println("Binary Expr: " + expr.op);
		tabsAmount++;
		
		PrintTabs();
		System.out.println("Left");
		tabsAmount++;
		expr.lhs.accept(this);
		tabsAmount--;
		
		PrintTabs();
		System.out.println("Right");
		tabsAmount++;
		expr.rhs.accept(this);
		tabsAmount--;
		
		
		tabsAmount--;
	}
	
	public void visit(If stmt)
	{
		PrintTabs();
		System.out.println("If Statement");
		tabsAmount++;
		
		PrintTabs();
		System.out.println("Condition");
		tabsAmount++;
		stmt.getCondition().accept(this);
		tabsAmount--;
		
		PrintTabs();
		System.out.println("If statement");
		tabsAmount++;
		stmt.getOperation().accept(this);
		tabsAmount--;
		
		if(stmt.hasElse())
		{
			PrintTabs();
			System.out.println("Else Statement");
			tabsAmount++;
			stmt.getElseOperation().accept(this);
			tabsAmount--;
		}
		
		tabsAmount--;
	}
	
	public void visit(Break stmt)
	{
		PrintTabs();
		System.out.println("Break Statement");
	}
	
	public void visit(Return stmt)
	{
		PrintTabs();
		System.out.println("Return Statement");
		
		tabsAmount++;
		if(stmt.hasValue())
		{
			stmt.getValue().accept(this);
		}
		tabsAmount--;
	}
	
	public void visit(VirtualFunctionCall funCall)
	{
		PrintTabs();
		System.out.println("Virtual Function Call - Name " + funCall.funcID);
		
		tabsAmount++;
		
		if(funCall.prefixExpr != null)
		{
			PrintTabs();
			System.out.println("Prefix Expression");
			tabsAmount++;
			funCall.prefixExpr.accept(this);
			tabsAmount--;
		}
		
		if(funCall.args != null)
		{
			PrintTabs();
			System.out.println("Arguments");
			tabsAmount++;
			funCall.args.accept(this);
			tabsAmount--;
		}
		
		tabsAmount--;
	}
	
	public void visit(StaticFunctionCall funCall)
	{
		PrintTabs();
		System.out.println("Static Function Call - Name " + funCall.funcID + ", Class Name " + funCall.classID);
		
		tabsAmount++;
		if(funCall.args != null)
		{
			PrintTabs();
			System.out.println("Arguments");
			tabsAmount++;
			funCall.args.accept(this);
			tabsAmount--;
		}
		
		tabsAmount--;
	}
	
	public void visit(CallStmt callStmt)
	{
		callStmt.getCall().accept(this);
	}
	
	public void visit(ExprLength eLength)
	{
		PrintTabs();
		System.out.println("Expression Length");
		
		tabsAmount++;
		eLength.e.accept(this);
		tabsAmount--;
	}
	
	public void visit(newClass nc)
	{
		PrintTabs();
		System.out.println("new Class " + nc.classID);
	}
	
	public void visit(newArray na)
	{
		PrintTabs();
		System.out.println("new Array - Type " + na.type.getName());
		tabsAmount++;
		
		PrintTabs();
		System.out.println("Array Length Expression");
		tabsAmount++;
		na.length.accept(this);
		tabsAmount--;
		
		tabsAmount--;
	}
	
	public void visit(ArgumentList lst)
	{
		lst.data.accept(this);
		if(lst.next != null)
		{
			lst.next.accept(this);
		}
	}
	
	public void visit(Continue con)
	{
		PrintTabs();
		System.out.println("Continue");
	}
	
	public void visit(This thisExpr)
	{
		PrintTabs();
		System.out.println("This");
	}
	
	public void visit(UnaryOpExpr expr)
	{
		PrintTabs();
		System.out.println("Unary Operation: " + expr.op);
		tabsAmount++;
		expr.operand.accept(this);
		tabsAmount--;
	}
}