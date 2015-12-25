package slp;

import java.util.*;

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
	public symbolTypes.ClassType currentClass = null;
	public symbolTypes.MethodType currentMethod = null;
	public boolean has_return_in_every_path;
	public String currentClassName;
	public List<String> GeneratedIRCode = new ArrayList<String>();
	public int iCurrentAvailableTempNumber;
	public int GlobalLabelCounter = 0;
	public int GlobalStringCounter = 0;
	public int LocalVarIndex = 0;
	public List<String> CurrentWhilesStartLabel = new ArrayList<String>();
	public List<String> CurrentWhilesExitLabel = new ArrayList<String>();
}