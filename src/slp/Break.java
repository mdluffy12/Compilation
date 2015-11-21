package src.slp;

public class Break extends Stmt {
	public Break(){}
	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}
	
}
