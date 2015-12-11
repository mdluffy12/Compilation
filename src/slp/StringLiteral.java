package slp;

public class StringLiteral extends Literal {
	public String value;
	public StringLiteral(String value, int line)
	{
		super(line);
		this.value = value;
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
