package slp;
import symbolTypes.*;
import java.util.HashMap;
import java.util.ArrayList;

public class SymbolTable {
	
	ArrayList<HashMap<String, SymbolType> > scopesHashTables;
	ArrayList<HashMap<String, SymbolType> > staticScopesHashTables;
	ArrayList<HashMap<String, SymbolType> > current;
	public SymbolTable()
	{
		scopesHashTables = new ArrayList<HashMap<String, SymbolType>>();
		staticScopesHashTables = new ArrayList<HashMap<String, SymbolType>>();
		current = scopesHashTables;
		StartScope();
	}
	
	public void StartStaticMode()
	{
		current = staticScopesHashTables;
	}
	
	public void StartVirtualMode()
	{
		current = scopesHashTables;
	}
	
	/*
	 * is variable declared in current scopt
	 */
	public boolean IsDeclaredInCurrentScope(String name)
	{
		return current.get(current.size()-1).get(name) != null;
	}
	/*
	 * get the closest variable with the same name as the given name
	 */
	public SymbolType GetClosestVarWithSameName(String name)
	{
		SymbolType data = null;
		for(int i = current.size() - 1; i >= 0; i--)
		{
			data = current.get(i).get(name);
			if(data != null)
			{
				break;
			}
		}
		return data;
	}
	
	/*
	 * starting new scope of a function, block, class, etc
	 */
	public void StartScope()
	{
		scopesHashTables.add(new HashMap<String, SymbolType>());
		staticScopesHashTables.add(new HashMap<String, SymbolType>());
	}
	
	/*
	 * exiting the scope and returning to the old scope
	 */
	public void ExitScope()
	{
		scopesHashTables.remove(scopesHashTables.size()-1);
		staticScopesHashTables.remove(staticScopesHashTables.size()-1);
	}
	
	public void InsertNewDecleration(String name, SymbolType data)
	{
		current.get(current.size() - 1).put(name, data);
	}
	
	public void InsertNewDeclerationAsBothStaticAndVirtual(String name, SymbolType data)
	{
		scopesHashTables.get(scopesHashTables.size() - 1).put(name, data);
		staticScopesHashTables.get(staticScopesHashTables.size() - 1).put(name, data);
	}
	
}
