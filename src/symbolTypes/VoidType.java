package symbolTypes;

public class VoidType extends SymbolType
{
	public VoidType()
	{
		super("VoidType");
	}
	
	public SymbolType Clone()
	{
		VoidType t1 = new VoidType();
		CopySymbolData(t1);
		return t1;
	}
	
	@Override
	public String toString() {
		return "void";
	}

}
