package src.slp;

public class Location extends Expr {
	public Location(){}
	@Override
	public void accept(Visitor visitor) {
		this.accept(visitor);
		
	}

	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return visitor.visit(this, context);
	}

}