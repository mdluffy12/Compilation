package slp;

/** An interface for AST visitors.
 */
public interface Visitor {
	public void visit(StmtList stmts);
	public void visit(Stmt stmt);
	public void visit(PrintStmt stmt);
	public void visit(AssignStmt stmt);
	public void visit(Expr expr);
	public void visit(ReadIExpr expr);
	public void visit(VarExpr expr);
	public void visit(NumberExpr expr);
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
}