package symbolTypes;

public class ArrType extends SymbolType{
	
	private SymbolType valType;
	private int dimension;
	public ArrType(SymbolType valType, int dimension)
	{
		super("ArrayType");
		this.valType=valType;
		this.dimension = dimension;
	}
	
	@Override
	public String toString() {
		String sStr = valType.toString();
		for(int i = 0; i < dimension; i++)
		{
			sStr += "[]";
		}
		return sStr;
	}
	
	public SymbolType getElemType() {
		return valType;
	}
	public int getDimension()
	{
		return dimension;
	}
}

