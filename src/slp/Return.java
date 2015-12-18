package slp;

public class Return extends Stmt {

	private Expr value = null;

	public Return(Expr e, int line,String comment) {
		super(line, comment);
		this.value = e;
	}

	public boolean hasValue() {
		return (value != null);
	}

	public Expr getValue() {
		return value;
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
