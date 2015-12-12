package symbolTypes;

public class BoolType extends SymbolType
{
	public BoolType()
	{
		this(false);
	}
	public BoolType(boolean isInitialized)
	{
		super("BoolType", isInitialized);
	}
	
	@Override
	public String toString() {
		return "boolean";
	}
}
