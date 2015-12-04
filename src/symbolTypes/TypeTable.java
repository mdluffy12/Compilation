package symbolTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeTable 
{
	private int typeID = 0;
	private Map<SymbolType, Integer> typesIDs = new HashMap<SymbolType, Integer>();
	private SymbolType intType = new IntType();
	private SymbolType stringType = new StringType();
	private SymbolType boolType = new BoolType();
	private SymbolType voidType = new VoidType();
	private SymbolType nullType = new NullType();
	private Map<SymbolType, ArrType> arrTypes = new HashMap<SymbolType, ArrType>();
	private Map<String, ClassType> classTypes = new HashMap<String, ClassType>();
	private Map<String, MethodType> methodTypes = new HashMap<String, MethodType>();

	public TypeTable() 
	{
		typesIDs.put(intType, ++typeID);
		typesIDs.put(boolType, ++typeID);
		typesIDs.put(nullType, ++typeID);
		typesIDs.put(stringType, ++typeID);
		typesIDs.put(voidType, ++typeID);
	}

	// convert slp.Type to symbolTypes.SymbolType
	public SymbolType getTypeFromAST(slp.Type node) {

		if (node instanceof slp.PrimitiveType) {
			slp.PrimitiveType primitiveType = (slp.PrimitiveType) node;
			if (primitiveType.getDimension() != 0) 
			{
				return getArrFromType(getPrimitiveType(node.getName()), primitiveType.getDimension());
			} 
			else 
			{
				return getPrimitiveType(node.getName());
			}
		} 
		else 
		{
			slp.ObjectClassType objType = (slp.ObjectClassType) node;
			if (objType.getDimension() != 0) 
			{
				return getArrFromType(classTypes.get(objType.getName()), objType.getDimension());
			} else {
				return classTypes.get(objType.getName());
			}
		}

	}

	public void addArrayTypeToDict(slp.Type node) 
	{

		SymbolType arrType;
		if (node instanceof slp.PrimitiveType) 
		{
			arrType = getPrimitiveType(node.getName());
		} 
		else 
		{
			arrType = classTypes.get(node.getName());
		}

		for (int i = 0; i < node.getDimension(); i++) 
		{
			arrType = updateArr(arrType);
		}
	}

	public Boolean addClassTypeToDict(slp.ICClass cls) {

		if (classTypes.containsKey(cls.getName())) 
		{
			return true;
		}

		ClassType superClassType;
		if (cls.hasSuperClass()) 
		{
			if (!classTypes.containsKey(cls.getSuperClassName())) 
			{
				return false;
			} 
			else 
			{
				superClassType = classTypes.get(cls.getSuperClassName());
				ClassType classType = new ClassType(cls.getName(),
						superClassType);
				classTypes.put(cls.getName(), classType);
				typesIDs.put(classType, ++typeID);
				return true;
			}
		} 
		else 
		{
			ClassType classType = new ClassType(cls.getName(), null);
			classTypes.put(cls.getName(), classType);
			typesIDs.put(classType, ++typeID);
			return true;
		}
	}

	public void addMethodTypeToDict(slp.ClassMethod method) 
	{
		MethodType methodType = createMethType(method);
		if (methodTypes.containsKey(methodType.toString())) 
		{
			return;
		} 
		else 
		{
			methodTypes.put(methodType.toString(), methodType);
			typesIDs.put(methodType, ++typeID);
		}
	}


	public SymbolType getPrimitiveType(String dataTypeName) 
	{
		if (dataTypeName == slp.DataTypes.INT.getDescription()) 
		{
			return intType;
		} 
		else if (dataTypeName == slp.DataTypes.STRING.getDescription()) 
		{
			return stringType;
		} 
		else if (dataTypeName == slp.DataTypes.VOID.getDescription()) 
		{
			return voidType;
		} 
		else if (dataTypeName == slp.DataTypes.BOOLEAN.getDescription()) 
		{
			return boolType;
		} 
		else 
		{
			return null;
		}
	}
	private ArrType updateArr(SymbolType type) {

		if (arrTypes.containsKey(type)) 
		{
			return arrTypes.get(type);
		}

		ArrType arrType = new ArrType(type);
		arrTypes.put(type, arrType);
		typesIDs.put(arrType, ++typeID);
		return arrType;
	}

	
	public SymbolType getTypeFromArr(SymbolType type) 
	{
		ArrType arrayType = (ArrType) type;
		return arrayType.getElemType();
	}
	
	public ClassType getClassTypeFromDict(String className) 
	{
		return classTypes.get(className);
	}
	
	public MethodType getMethodTypeFromDict(slp.ClassMethod method) 
	{
		return methodTypes.get(createMethType(method).toString());
	}

	public ArrType getArrFromType(SymbolType type, int dimention) 
	{
		SymbolType currArrType = type;
		for (int i = 0; i < dimention; i++) 
		{
			currArrType = arrTypes.get(currArrType);
		}
		return (ArrType) currArrType;
	}

	private MethodType createMethType(slp.ClassMethod method) 
	{
		List<slp.MethodFormal> formals = method.getFormals();
		SymbolType[] prms;

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
				prms[i] = getTypeFromAST(formals.get(i).getType());
			}
		}
		MethodType method_type = new MethodType(prms, getTypeFromAST(method.getType()));
		return method_type;
	}

}
