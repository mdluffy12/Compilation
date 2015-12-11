package slp;

public class ArrValueLocation extends ValueLocation {
	public Expr baseExpression;
	public Expr indexExpression;
	
	public ArrValueLocation(Expr baseExpr, Expr indExpr, int line)
	{
		super(line);
		this.baseExpression = baseExpr;
		this.indexExpression = indExpr;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);

	}

	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}
	
}
