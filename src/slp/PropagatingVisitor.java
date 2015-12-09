package slp;

/** An interface for a propagating AST visitor.
 * The visitor passes down objects of type <code>DownType</code>
 * and propagates up objects of type <code>UpType</code>.
 */
public interface PropagatingVisitor<DownType,UpType> {
	public UpType visit(StmtList stmts, DownType d);
	public UpType visit(AssignStmt stmt, DownType d);
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
	public UpType visit(newArray arr, DownType d);
	public UpType visit(BlockStmt blk, DownType d);
	public UpType visit(LocalVar lvar, DownType d);
	public UpType visit(BoolLiteral bl, DownType d);
	public UpType visit(Break br, DownType d);
	public UpType visit(CallStmt cstmt, DownType d);
	public UpType visit(Continue con, DownType d);
	public UpType visit(ExprLength exprLength, DownType d);
	public UpType visit(If ifStmt, DownType d);
	public UpType visit(IntLiteral intLit, DownType d);
	public UpType visit(newClass nclss, DownType d);
	public UpType visit(NullLiteral nllLit, DownType d);
	public UpType visit(Return retStmt, DownType d);
	public UpType visit(StaticFunctionCall sfc, DownType d);
	public UpType visit(StringLiteral slit, DownType d);
	public UpType visit(This thisStmt, DownType d);
	public UpType visit(VarValueLocation varValLoc, DownType d);
	public UpType visit(VirtualFunctionCall vfc, DownType d);
	public UpType visit(While whileStmt, DownType d);
	public UpType visit(ArrValueLocation arrValLoc, DownType d);
}