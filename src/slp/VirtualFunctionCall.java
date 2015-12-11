package slp;

public class VirtualFunctionCall extends FunctionCall {
	public Expr prefixExpr;
	public String funcID;
	public ArgumentList args;
	public VirtualFunctionCall(String funcID, ArgumentList args, Expr prefix, int line)
	{
		super(line);
		this.prefixExpr = prefix;
		this.funcID = funcID;
		this.args = args;
	}
	public VirtualFunctionCall(String funcID, ArgumentList args, int line)
	{
		this(funcID, args, null, line);
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
