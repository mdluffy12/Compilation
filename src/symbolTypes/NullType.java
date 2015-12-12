package symbolTypes;

public class NullType extends SymbolType
{
	public NullType()
	{
		this(false);
	}
	
	public NullType(boolean isInitialized)
	{
		super("NullType", isInitialized);
	}
	@Override
	public String toString()
	{
		return "null";
	}
}
