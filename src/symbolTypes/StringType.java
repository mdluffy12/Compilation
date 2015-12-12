package symbolTypes;

public class StringType extends SymbolType
{
	public StringType()
	{
		this(false);
	}
	
	public StringType(boolean isInitialized)
	{
		super("StringType", isInitialized);
	}
	
	@Override
	public String toString() 
	{
		return "string";
	}

}
