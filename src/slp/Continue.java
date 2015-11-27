package slp;

public class Continue extends Stmt {
	public Continue(){}
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
		
	}
	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}

}
