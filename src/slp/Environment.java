package slp;

import java.util.*;

import symbolTypes.TypeTable;

/** Represents a state during the evaluation of a program. 
 */
public class Environment {
	/** Maps the names of variables to integer values.
	 * The same variable may appear in different VarExpr objects.  We use the
	 * name of the variable as a way of ensuring we a consistent mapping
	 * for each variable. 
	 */
	public int loop_counter = 0; /* counts in how many loops we are in (for example 2 nested loops; the inside of them would be count = 2)*/
	public SymbolTable symbolTable = new SymbolTable();
}