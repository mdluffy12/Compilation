package slp;

public class ArrValueLocation extends ValueLocation {
	public Expr baseExpression;
	public Expr indexExpression;
	
	public ArrValueLocation(Expr baseExpr, Expr indExpr)
	{
		this.baseExpression = baseExpr;
		this.indexExpression = indExpr;
	}

}
