package symbolTypes;

public class IntType extends SymbolType
{
	public IntType()
	{
		super("IntType");
	}
	
	public SymbolType Clone()
	{
		IntType t1 = new IntType();
		CopySymbolData(t1);
		return t1;
	}
	
	@Override
	public String toString() 
	{
		return "int";
	}
}
