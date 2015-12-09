package symbolTypes;

public class MethodType extends SymbolType 
{
	SymbolType[] parTypes;
	SymbolType retType;
	boolean isStaticMethod;
	public MethodType(SymbolType[] parTypes,SymbolType retType, boolean isStatic)
	{
		super("MethodType");
		this.parTypes = parTypes;
		this.retType = retType;
		this.isStaticMethod = isStatic;
	}
	
	@Override
	public String toString() 
	{
		String str = "";
		if(isStaticMethod)
		{
			str = "STATIC ";
		}
		str = parTypes[0].toString();
		for (int i = 1; i < parTypes.length; i++) 
		{
			str += ", " + parTypes[i];
		}
		return str + " -> " + retType.toString();
	}
	public SymbolType[] getArgs()
	{
		return parTypes;
	}
	public SymbolType getRetType()
	{
		return retType;
	}

}
