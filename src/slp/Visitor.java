package slp;

/** An interface for AST visitors.
 */
public interface Visitor {
	public void visit(StmtList stmts);
	public void visit(LocalVar stmt);
	public void visit(AssignStmt stmt);
	public void visit(BlockStmt stmt);
	public void visit(While stmt);
	public void visit(If stmt);
	public void visit(Break stmt);
	public void visit(Return stmt);
	public void visit(ExprLength eLength);
	public void visit(newClass nc);
	public void visit(newArray na);
	public void visit(VirtualFunctionCall funCall);
	public void visit(StaticFunctionCall funCall);
	public void visit(CallStmt callStmt);
	public void visit(NullLiteral lit);
	public void visit(IntLiteral lit);
	public void visit(StringLiteral lit);
	public void visit(BoolLiteral lit);
	public void visit(VarValueLocation expr);
	public void visit(ArrValueLocation expr);
	public void visit(UnaryOpExpr expr);
	public void visit(BinaryOpExpr expr);
	public void visit(Program prog);
	public void visit(ICClass cls);
	public void visit(ClassField field);
	public void visit(MethodFormal formal);
	public void visit(StaticMethod method);
	public void visit(VirtualMethod method);
	public void visit(PrimitiveType ptype);
	public void visit(ObjectClassType octype);
	public void visit(ArgumentList lst);
	public void visit(Continue con);
	public void visit(This thisExpr);
}