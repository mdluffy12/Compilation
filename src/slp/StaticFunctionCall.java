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
}
