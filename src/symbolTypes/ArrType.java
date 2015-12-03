package symbolTypes;

public class ArrType extends SymbolType{
	
	private SymbolType valType;
	public ArrType(SymbolType valType)
	{
		super("ArrayType");
		this.valType=valType;
	}
	
	@Override
	public String toString() {
		return valType.toString() + "[]";
	}
	
	public SymbolType getElemType() {
		return valType;
	}
}

