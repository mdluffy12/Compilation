package src.slp;

public class This extends Expr {
	public This()
	{
		
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);

	}
	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return visitor.visit(this, context);
	}
}
