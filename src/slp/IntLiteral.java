package slp;

public class IntLiteral extends Literal {
	public int value;
	public IntLiteral(int iValue, int line)
	{
		super(line);
		this.value = iValue;
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
