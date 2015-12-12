package symbolTypes;

public class IntType extends SymbolType
{
	public IntType()
	{
		this(false);
	}
	
	public IntType(boolean isInitialized)
	{
		super("IntType", isInitialized);
	}
	
	@Override
	public String toString() 
	{
		return "int";
	}
}
