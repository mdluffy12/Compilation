package symbolTypes;

public class NullType extends SymbolType
{
	public NullType()
	{
		super("NullType");
	}
	
	public SymbolType Clone()
	{
		NullType t1 = new NullType();
		CopySymbolData(t1);
		return t1;
	}
	
	@Override
	public String toString()
	{
		return "null";
	}
}
