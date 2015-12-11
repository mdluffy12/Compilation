package slp;

public class ExprLength  extends Expr{
	public final Expr e;
	
	public ExprLength(Expr e, int line) {
		super(line);
		this.e = e;
	}
	

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return visitor.visit(this, context);
	}
	
}
