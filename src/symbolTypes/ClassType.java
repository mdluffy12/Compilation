package symbolTypes;

import java.util.HashMap;

public class ClassType extends SymbolType
{
	String name;
	ClassType superClass;
	HashMap<String, SymbolType> classMembersAndFucntions;

	public ClassType(String name, ClassType superClass)
	{
		super("ClassType");
		this.name = name;
		this.superClass = superClass;
		this.classMembersAndFucntions = new HashMap<String, SymbolType>();
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
	
	public void AddNewMember(String sName, SymbolType t1)
	{
		if(classMembersAndFucntions.get(sName) != null)
		{
			throw new RuntimeException("error you cannot override member in the class");
		}
		classMembersAndFucntions.put(sName, t1);
	}
	
	public SymbolType GetMemberFromCurrentClassOnly(String sName)
	{
		return classMembersAndFucntions.get(sName);
	}
	
	public SymbolType GetMemberFromMeOrClosestParent(String sName)
	{
		SymbolType type1 = GetMemberFromCurrentClassOnly(sName);
		if(superClass != null && type1 == null)
		{
			type1 = superClass.GetMemberFromMeOrClosestParent(sName);
		}
		return type1;
	}

}
