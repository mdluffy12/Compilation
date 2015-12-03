package slp;

import java.io.IOException;

import symbolTypes.*;
import slp.ClassField;
import slp.Environment;
import slp.ICClass;
import slp.MethodFormal;
import slp.ObjectClassType;
import slp.PrimitiveType;
import slp.Program;
import slp.StaticMethod;
import slp.VirtualMethod;

/** Evaluates straight line programs.
 */
public class SLPEvaluator implements PropagatingVisitor<Environment, symbolTypes.SymbolType> 
{
	protected ASTNode root;

	/** Constructs an SLP interpreter for the given AST.
	 * 
	 * @param root An SLP AST node.
	 */
	public SLPEvaluator(ASTNode root) {
		this.root = root;
	}
	
	/** Interprets the AST passed to the constructor.
	 */
	public void evaluate() {
		Environment env = new Environment();
		root.accept(this, env);
	}
	
	public Integer visit(StmtList stmts, Environment env) {
		for (Stmt st : stmts.statements) {
			st.accept(this, env);
		}
		return null;
	}

	public Integer visit(Stmt stmt, Environment env) {
		throw new UnsupportedOperationException("Unexpected visit of Stmt!");
	}

	public Integer visit(PrintStmt stmt, Environment env) {
		Integer printValue = stmt.expr.accept(this, env);
		System.out.println(printValue);
		return null;
	}

	public Integer visit(AssignStmt stmt, Environment env) {
		Expr rhs = stmt.rhs;
		Integer expressionValue = rhs.accept(this, env);
		VarExpr var = stmt.varExpr;
		env.update(var, expressionValue);
		return null;
	}

	public Integer visit(Expr expr, Environment env) {
		throw new UnsupportedOperationException("Unexpected visit of Expr!");
	}

	public Integer visit(ReadIExpr expr, Environment env) {
		int readValue;
		try {
			System.out.println("Enter number: ");
			readValue = System.in.read();
		}
		catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return new Integer(readValue);
		// return readValue; also works in Java 1.5 because of auto-boxing
	}

	public Integer visit(VarExpr expr, Environment env) {
		return env.get(expr);
	}

	public Integer visit(NumberExpr expr, Environment env) {
		return new Integer(expr.value);		
		// return expr.value; also works in Java 1.5 because of auto-boxing
	}
	
	
	public SymbolType visit(newArray arr, Environment env) {
		SymbolType typeSize = arr.length.accept(this, env);

		if (typeSize == null || !typeSize.isIntType()) {
			throw new RuntimeException(newArray.getLine()+
					": Size of array must be an integer number.");
		}

		// TODO: add array to symbol table
		return new symbolTypes.ArrType(arr.getNodeType());
	}

	public SymbolType visit(UnaryOpExpr expr, Environment env) 
	{
		SymbolType type = expr.operand.accept(this, env);
		// Check for semantic error
		if (type == null) 
		{
			throw new RuntimeException(expr.getLine()+
					": can not perform unary operation on a VOID type parameter.");
		}
		switch (expr.op) 
		{
			case UMINUS:
				if (type.isIntType()) 
				{
					return new symbolTypes.IntType();
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid type in an umary minus opertaion.");
				}
				break;
			case LNEG:
				if (type.isBoolType())
				{
					return new symbolTypes.BoolType();
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid type in an umary 'not' opertaion.");
				}
				break;
			default:
				throw new RuntimeException(expr.getLine()+
						": Invalid type in an umary opertaion.");
				break;
		}
	}

	public SymbolType visit(BinaryOpExpr expr, Environment env) 
	{
		SymbolType type1 = expr.lhs.accept(this, env);
		SymbolType type2 = expr.rhs.accept(this, env);
		
		if (type1 == null || type2 == null) {
			throw new Exception(expr.getLine()+
					": can not perform binary operation on a VOID type parameter.");
		}

		switch (expr.op) 
		{
			case PLUS:
				if (type1.isIntType() && type2.isIntType()) 
				{
					return new symbolTypes.IntType();
				}
				else if (type1.isStringType() && type2.isStringType())
				{
					return new symbolTypes.StringType();
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid  usage of a arithmetic operation <" + expr.op.toString() +">" );
				}
			case MINUS:
			case MULT:
			case DIV:
			case MOD:
				if (type1.isIntType() && type2.isIntType()) {
					return new symbolTypes.IntType();
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
							": Invalid  usage of a arithmetic operation <" + expr.op.toString() +">." );
				}
				throw new RuntimeException(expr.getLine()+
						": Invalid  usage of a arithmetic operation <" + expr.op.toString() + "> with a non-integer expression.");
			case LT:
			case GT:
			case LE:
			case GE:
				if (type1.isIntType() && type2.isIntType())
				{
					return new symbolTypes.BoolType();
				}
				else
				{
					throw new RuntimeException(expr.getLine()+
						": Invalid  usage of a binary operation <"+ expr.op.toString()+ "> with a non-integer expression.");
				}
				break;
			case LAND:
			case LOR:
				if (type1.isBoolType() && type2.isBoolType())
				{
					return new symbolTypes.BoolType();
				}
				else
				{
				throw new RuntimeException(expr.getLine()+
						": Invalid usage of a binary operation <"+ expr.op.toString()+ "> with a non-boolean expression.");
				}
				break;
			case NEQUAL:
			case EQUAL:
				if (type1.equals(type2))
					return new symbolTypes.BoolType();
				if ((type1.isNullable()) && (type2.isNullType()) || (type1.isNullType()) && (type2.isNullable()))
					return new symbolTypes.BoolType();
				if ((type1.subTypeOf(type2)) || (type2.subTypeOf(type1)))
					return new symbolTypes.BoolType();
				throw new RuntimeException(expr.getLine()+
						": Invalid usage of a binary operation <"+ expr.op.toString()+ "> with a mismatching expression.");
			default:
				throw new RuntimeException(expr.getLine()+
						": Invalid use of a binary operation.");
				break;
		}
	}
	public SymbolType visit(Program prog, Environment env)
	{	
		SymbolTable programSymTable = // somhow get the program's symbol table
		
		for (ICClass cls : prog.getClasses()) 
		{

			String classId = cls.getName();
			// TODO: classType = getClassTypeFromSymbolTable(classId)
			Category classCtrg = Category.CLASS;
			// add class to symbolTable

			// add the class as an entry in the program's SymbolTable.


			// Set the ASTNode's entry type.
			cls.setEntryType(classType); // decleration of classType is in comment. implement and it will work

			// set Parent Symbol Table:
			SymbolTable parentSymTable;

			if (cls.hasSuperClass()) 
			{
				// add enable way to get symbols from parnt symbol table
				parentSymTable
			} 
			else 
			{
				parentSymTable = programSymTable;
			}

			// Create Class'es Symbol Table
			SymbolTable classSymTable = new SymbolTable(); // params??

			// Set parent table pointers
			// here? in symbolTable class?
		}

		return null;
	}
	public SymbolType visit(ICClass cls, Environment env)
	{
		String classId = cls.getName();
		SymbolTable classSymTable = // somhow get the program's symbol table
		// Add all Fields - Members:
		for (ClassMember member : cls.getMembers()) {

			// Initialize needed parameters:
			if (member instanceof ClassField)
			{
				ClassField field = (ClassField)member;
				String fieldId = field.getName();
				symbolTypes.SymbolType fieldType = // get type from table
				Category fieldCategoty = Category.FIELD; 
				
				// cant have other vars with the same name
				// entry = getFromSymbolTable (fieldId, symbolTable)
				if (entry != null) // TODO: implement the above code, and use the entry
				{
					throw new RuntimeException(field.getLine()+": Variable shadowing is not possible for field "+ fieldId + "."));
				}
				
				// add the field to the symbol table
				if(add == false)
				{
					throw new RuntimeException(field.getLine()+": field "+ field.getName() +" is declared more than once")
				}
				
				field.setNodeType(fieldType);
			}
			if (member instanceof ClassMethod)
			{
				ClassMethod method = (ClassMethod)member;
				String methodId = method.getName();
				symbolTypes.SymbolType methodType = // get type from table
						
				// add the method to the symbol table
				if (add == false)
				{
					throw new RuntimeException(method.getLine()+": method "+ method.getName() +" is declared more than once")
				}
				
				method.setNodeType(methodType);
				SymbolTable mathodTable = new SymbolTable (); // new symbolTable for mathod
				//add stuff to method's table
			}
			

		}
		return null;
	}
	public Integer visit(ClassField field, Environment env)
	{
		return null;
	}
	public Integer visit(MethodFormal formal, Environment env)
	{
		return null;
	}
	public Integer visit(StaticMethod method, Environment env)
	{
		return null;
	}
	public Integer visit(VirtualMethod method, Environment env)
	{
		return null;
	}
	public Integer visit(PrimitiveType ptype, Environment env)
	{
		return null;
	}
	public Integer visit(ObjectClassType octype, Environment env)
	{
		return null;
	}
}