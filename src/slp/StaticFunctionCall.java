package slp;

public class StaticFunctionCall extends FunctionCall {
	public String classID;
	public String funcID;
	public ArgumentList args;
	public StaticFunctionCall(String classID, String funcID, ArgumentList args)
	{
		this.classID = classID;
		this.funcID = funcID;
		this.args = args;
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
