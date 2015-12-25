package symbolTypes;

public class StringType extends SymbolType
{
	public StringType()
	{
		super("StringType");
	}
	public SymbolType Clone()
	{
		StringType t1 = new StringType();
		CopySymbolData(t1);
		return t1;
	}
	@Override
	public String toString() 
	{
		return "string";
	}

}
