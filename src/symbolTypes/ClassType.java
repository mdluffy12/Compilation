package symbolTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassType extends SymbolType
{
	String name;
	ClassType superClass;
	HashMap<String, SymbolType> classMembersAndFucntions;
	List<String> VirtualTable;
	public int iAmountOfVariables;
	public ClassType(String name, ClassType superClass)
	{
		super("ClassType");
		this.name = name;
		this.superClass = superClass;
		this.classMembersAndFucntions = new HashMap<String, SymbolType>();
		VirtualTable = null;
		iAmountOfVariables = 1; // 1 for the virtual table - it is a required one
	}
	
	public SymbolType Clone()
	{
		ClassType t1 = new ClassType(name, superClass);
		t1.classMembersAndFucntions = classMembersAndFucntions;
		t1.VirtualTable = VirtualTable;
		t1.iAmountOfVariables = iAmountOfVariables;
		CopySymbolData(t1);
		return t1;
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
	
	public List<String> GetVirtualTable()
	{
		return VirtualTable;
	}
	
	public void SetVirtualTable(List<String> vTable)
	{
		VirtualTable = vTable;
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
