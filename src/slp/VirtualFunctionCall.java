package slp;

public class VirtualFunctionCall extends FunctionCall {
	public Expr prefixExpr;
	public String funcID;
	public ArgumentList args;
	public VirtualFunctionCall(String funcID, ArgumentList args, Expr prefix)
	{
		this.prefixExpr = prefix;
		this.funcID = funcID;
		this.args = args;
	}
	public VirtualFunctionCall(String funcID, ArgumentList args)
	{
		this.prefixExpr = null;
		this.funcID = funcID;
		this.args = args;
	}
}
