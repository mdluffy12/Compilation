package slp;

public class NullLiteral extends Literal {
	
	public NullLiteral(int line)
	{
		super(line);
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
