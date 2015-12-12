package symbolTypes;

public class VoidType extends SymbolType
{
	public VoidType()
	{
		this(false);
	}
	
	public VoidType(boolean isInitialized)
	{
		super("VoidType", isInitialized);
	}
	
	@Override
	public String toString() {
		return "void";
	}

}
