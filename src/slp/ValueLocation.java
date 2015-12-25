package slp;

public abstract class ValueLocation extends Expr {
	boolean bIsLeftSideOfAssignStmt;
	String sAssignIRName;
	protected ValueLocation(int line)
	{
		super(line);
		bIsLeftSideOfAssignStmt = false;
	}

}
