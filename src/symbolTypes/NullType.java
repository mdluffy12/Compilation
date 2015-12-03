package symbolTypes;

public class NullType extends SymbolType
{
	public NullType()
	{
		super("NullType");
	}
	
	@Override
	public String toString()
	{
		return "null";
	}
}
