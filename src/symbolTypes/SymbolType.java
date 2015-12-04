package symbolTypes;

public abstract class SymbolType {
	protected String name;
	
	public SymbolType(String name) 
	{
		this.name=name;
	}
	
	public boolean compareType(SymbolType otherType)
	{
		return (this.equals(otherType));
	}
	
	public boolean subTypeOf(SymbolType type)
	{
		if (this.equals(type))
			return true;
		if ((this.isNullType()) && (type.isClassType()))
			return true;
		if (this.isClassType()) {
			ClassType classType = (ClassType)this;
			if (classType.hasSuperClass())
				return classType.getSuperClass().subTypeOf(type);
		}
		return false;
	}
	
	public boolean isArrType() {
		return (this instanceof ArrType);
	}
	
	public boolean isBoolType() {
		return (this instanceof BoolType);
	}
	
	public boolean isClassType() {
		return (this instanceof ClassType);
	}
	
	public boolean isIntType() {
		return (this instanceof IntType);
	}
	
	public boolean isMethType(){
		return (this instanceof MethodType);
	}
	
	public boolean isStringType() {
		return (this instanceof StringType);
	}
	
	public boolean isVoidType() {
		return (this instanceof VoidType);
	}
	
	public boolean isNullType() {
		return (this instanceof NullType);
	}
	
	public boolean isNullable() {
		
		if (this instanceof MethodType) {
			MethodType methType = (MethodType)this;
			return methType.retType.isNullable();
		}
		
		if ((this.isClassType()) || (this.isArrType()) || (this.isStringType()))
			return true;
		return false;
	}
	public String getName()
	{
		return this.name;
	}
}
