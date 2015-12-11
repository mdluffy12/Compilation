package slp;

public class BlockStmt extends Stmt {

	public StmtList m_stmtList;
	public BlockStmt(StmtList stmtList, int line)
	{
		super(line);
		m_stmtList = stmtList;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}	

}
