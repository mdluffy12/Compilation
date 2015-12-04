package slp;
import symbolTypes.*;

public class SymbolTable {
	
	public SymbolTable()
	{
		
	}
	
	/*
	 * is variable declared in current scopt
	 */
	public boolean IsDeclaredInCurrentScope(String name)
	{
		return true;
	}
	/*
	 * get the closest variable with the same name as the given name
	 */
	public SymbolType GetClosestVarWithSameName(String name)
	{
		return null;
		
	}
	
	/*
	 * starting new scope of a function, block, class, etc
	 */
	public void StartScope()
	{
		
	}
	
	/*
	 * exiting the scope and returning to the old scope
	 */
	public void ExitScope()
	{
		
	}
	
	public void InsertNewDecleration(String name, SymbolType t1)
	{
		
	}
	
}
