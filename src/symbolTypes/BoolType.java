package symbolTypes;

public class BoolType extends SymbolType
{
	public BoolType()
	{
		super("BoolType");
	}
	
	public SymbolType Clone()
	{
		BoolType t1 = new BoolType();
		CopySymbolData(t1);
		return t1;
	}
	
	@Override
	public String toString() {
		return "boolean";
	}
}
