package slp;

/** An interface for a propagating AST visitor.
 * The visitor passes down objects of type <code>DownType</code>
 * and propagates up objects of type <code>UpType</code>.
 */
public interface PropagatingVisitor<DownType,UpType> {
	public UpType visit(StmtList stmts, DownType d);
	public UpType visit(Stmt stmt, DownType d);
	public UpType visit(AssignStmt stmt, DownType d);
	public UpType visit(Expr expr, DownType d);
	public UpType visit(ReadIExpr expr, DownType d);
	public UpType visit(UnaryOpExpr expr, DownType d);
	public UpType visit(BinaryOpExpr expr, DownType d);
	public UpType visit(Program prog, DownType d);
	public UpType visit(ICClass cls, DownType d);
	public UpType visit(ClassField field, DownType d);
	public UpType visit(MethodFormal formal, DownType d);
	public UpType visit(StaticMethod method, DownType d);
	public UpType visit(VirtualMethod method, DownType d);
	public UpType visit(PrimitiveType ptype, DownType d);
	public UpType visit(ObjectClassType octype, DownType d);
	
	

}