package symbolTypes;

public class ClassType extends SymbolType
{
	String name;
	ClassType superClass;

	public ClassType(String name, ClassType superClass)
	{
		super("ClassType");
		this.name = name;
		this.superClass = superClass;
	}
	
	public ClassType getSuperClass() {
		return superClass;
	}

	public Boolean hasSuperClass() {
		return (superClass != null);
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
