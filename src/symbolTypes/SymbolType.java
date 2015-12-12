package symbolTypes;

import java.util.List;

import slp.StaticMethod;
import slp.VirtualMethod;

public abstract class SymbolType {
	protected String name;
	protected boolean isInitialized; 
	static private SymbolType intType = new IntType();
	static private SymbolType stringType = new StringType();
	static private SymbolType boolType = new BoolType();
	static private SymbolType voidType = new VoidType();
	static private SymbolType nullType = new NullType();
	
	public SymbolType(String name, boolean isInitialized) 
	{
		this.name=name;
		this.isInitialized = isInitialized;
	}
	
	public boolean isInitialized()
	{
		return this.isInitialized;
	}
	
	public void initialize()
	{
		this.isInitialized = true;
	}
	
	static public SymbolType getTypeFromAST(slp.Type node, slp.SymbolTable table) {

		if (node instanceof slp.PrimitiveType) {
			slp.PrimitiveType primitiveType = (slp.PrimitiveType) node;
			if (primitiveType.getDimension() != 0) 
			{
				return new ArrType(getPrimitiveType(node.getName()), primitiveType.getDimension());
			} 
			else 
			{
				return getPrimitiveType(node.getName());
			}
		} 
		else 
		{
			slp.ObjectClassType objType = (slp.ObjectClassType) node;
			SymbolType basicType = table.GetClosestVarWithSameName(objType.getName());
			if(basicType == null)
			{
				return null;
			}
			if (objType.getDimension() != 0) 
			{
				return new ArrType(basicType, objType.getDimension());
			} else {
				return basicType;
			}
		}

	}
	
	static public SymbolType getPrimitiveType(String dataTypeName) 
	{
		if (dataTypeName == slp.DataTypes.INT.getDescription()) 
		{
			return new IntType();
		} 
		else if (dataTypeName == slp.DataTypes.STRING.getDescription()) 
		{
			return new StringType();
		} 
		else if (dataTypeName == slp.DataTypes.VOID.getDescription()) 
		{
			return new VoidType();
		}
		else if (dataTypeName == slp.DataTypes.BOOLEAN.getDescription()) 
		{
			return new BoolType();
		} 
		else 
		{
			return null;
		}
	}
	
	static public MethodType createMethType(slp.ClassMethod method, slp.SymbolTable table) 
	{
		List<slp.MethodFormal> formals = method.getFormals();
		SymbolType[] prms;
		
		boolean bIsStatic;
		
		if(method instanceof StaticMethod)
		{
			bIsStatic = true;
		}
		else if(method instanceof VirtualMethod)
		{
			bIsStatic = false;
		}
		else
		{
			throw new RuntimeException(method.getLine()+
					":" + "Unknown method type");
		}

		if (method.getFormals().size() == 0) 
		{
			prms = new SymbolType[1];
			prms[0] = new VoidType();
		} 
		else 
		{
			prms = new SymbolType[method.getFormals().size()];
			for (int i = 0; i < prms.length; i++) 
			{
				prms[i] = getTypeFromAST(formals.get(i).getType(), table);
				if(prms[i] == null)
				{
					return null;
				}
			}
		}
		SymbolType retType = getTypeFromAST(method.getType(), table);
		if(retType == null)
		{
			return null;
		}
		MethodType method_type = new MethodType(prms, retType, bIsStatic);
		return method_type;
	}
	
	public boolean compareType(SymbolType otherType)
	{
		return (this.toString().equals(otherType.toString()));
	}
	
	public boolean subTypeOf(SymbolType type)
	{
		if (this.compareType(type))
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
