package slp;

public class VarValueLocation extends ValueLocation {

	public String varID;
	public boolean UseVarExpr;
	public Expr varExpression;
	
	public VarValueLocation(String varID)
	{
		this.varID = varID;
		this.UseVarExpr = false;
		this.varExpression = null;
	}
	
	public VarValueLocation(Expr varExpression, String varID)
	{
		this.varID = varID;
		this.UseVarExpr = true;
		this.varExpression = varExpression;
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
