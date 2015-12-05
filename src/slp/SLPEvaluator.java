package slp;

import java.io.IOException;

import IC.AST.Break;
import IC.AST.Continue;
import IC.AST.Expression;
import IC.AST.Formal;
import IC.AST.If;
import IC.AST.Method;
import IC.AST.Statement;
import IC.AST.While;
import IC.Symbol.Kind;
import IC.Symbol.Symbol;
import IC.Symbol.SymbolTable;
import IC.Symbol.SymbolTableType;
import jdk.nashorn.internal.ir.BlockStatement;
import symbolTypes.*;
import slp.BlockStmt;
import slp.ClassField;
import slp.Environment;
import slp.ICClass;
import slp.LocalVar;
import slp.MethodFormal;
import slp.ObjectClassType;
import slp.PrimitiveType;
import slp.Program;
import slp.StaticMethod;
import slp.Stmt;
import slp.VirtualMethod;

/** Evaluates straight line programs.
 */
public class SLPEvaluator implements PropagatingVisitor<Environment, symbolTypes.SymbolType> 
{
	protected ASTNode root;
	protected TypeTable type_t;
	protected SymbolTable table;

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
	
	public SymbolType visit(StmtList stmts, Environment env) 
	{
		for (Stmt st : stmts.statements) 
		{
			st.accept(this, env);
		}
		return null;
	}

	public SymbolType visit(Stmt stmt, Environment env) 
	{
		throw new RuntimeException("Unexpected visit of Stmt!");
	}

	public SymbolType visit(AssignStmt stmt, Environment env) 
	{
		SymbolType type1 = stmt.varExpr.accept(this, env);
		SymbolType type2 = stmt.rhs.accept(this, env);
		
		if (type1 == null || type2 == null) 
		{
			throw new RuntimeException(stmt.getLine()+
					": can not perform assignment from type void, or to type void.");
		}
		
		if(!type1.compareType(type2))
		{
			throw new RuntimeException(stmt.getLine()+
					": can not perform assignment from type "+type2.toString() +" to type "+type1.toString());
		}
	}

	public SymbolType visit(Expr expr, Environment env) {
		throw new RuntimeException("Unexpected visit of Expr!");
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
			default:
				throw new RuntimeException(expr.getLine()+
						": Invalid type in an umary opertaion.");
		}
	}

	public SymbolType visit(BinaryOpExpr expr, Environment env) 
	{
		SymbolType type1 = expr.lhs.accept(this, env);
		SymbolType type2 = expr.rhs.accept(this, env);
		
		if (type1 == null || type2 == null) {
			throw new RuntimeException(expr.getLine()+
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
		}
	}
	public SymbolType visit(Program prog, Environment env)
	{	
		table = new SymbolTable();// somhow get the program's symbol table
		
		for (ICClass cls : prog.getClasses()) 
		{
			String classId = cls.getName();
			symbolTypes.SymbolType classType = type_t.getClassTypeFromDict(classId);
			
			cls.setNodeType(classType);
			
			// add class to symbolTable
			table.InsertNewDecleration(classId, classType);
			table.StartScope();
			
			cls.accept(this, env); // get all the relevant methods and variables of the class
			
			if (cls.hasSuperClass()) 
			{
				// add enable way to get symbols from parnt symbol table
			} 
			else 
			{
			}
			
			table.ExitScope();
		}

		return null;
	}
	public SymbolType visit(ICClass cls, Environment env)
	{
		String classId = cls.getName();
		//SymbolTable classSymTable = // somehow get the program's symbol table
		// Add all Members:
		for (ClassMember member : cls.getMembers()) {

			// Initialize needed parameters:
			if (member instanceof ClassField)
			{
				ClassField field = (ClassField)member;
				String fieldId = field.getName();
				symbolTypes.SymbolType fieldType = type_t.getTypeFromAST(field.getType());
				field.setNodeType(fieldType);
				// Can't have other variables with the same name
				
				// entry = getFromSymbolTable (fieldId, symbolTable)
				if (table.IsDeclaredInCurrentScope(fieldId))
				{
					throw new RuntimeException(field.getLine()+": Variable shadowing is not possible for field "+ fieldId + "."));
				}
				
				// add the field to the symbol table
				if(!table.InsertNewDecleration(fieldId, fieldType))
				{
					throw new RuntimeException(field.getLine()+": field "+ field.getName() +" is declared more than once");
				}
				
			
				field.accept(this, env);
				
			}
			if (member instanceof ClassMethod)
			{
				ClassMethod method = (ClassMethod)member;
				String methodId = method.getName();
				symbolTypes.SymbolType methodType = type_t.getMethodTypeFromDict(method);
				method.setNodeType(methodType);
						
				// add the method to the symbol table
				if (!table.InsertNewDecleration(methodId, methodType))
				{
					throw new RuntimeException(method.getLine()+": method "+ method.getName() +" is declared more than once");
				}
				// method scope
				table.StartScope();
				
				method.accept(this, env);
				
				table.ExitScope();
			}
			

		}
		return null;
	}
	
	public SymbolType visit(ClassField field, Environment env)
	{
		if(!env.symbolTable.IsDeclaredInCurrentScope(field.getName()))
		{
			throw new RuntimeException(field.getLine() + ": field" + field.getName() + "is declared more than once.");
		}
		else
		{
			SymbolType fieldSymbol = new SymbolType(field.getName());
			field.getType().accept(this, env);
			return fieldSymbol;
		}
	}
	
	public SymbolType visit(MethodFormal formal, Environment env)
	{
		String formalId = formal.getName();
		SymbolType formalType = type_t.getTypeFromAST(formal.getType());
		String formalID = formal.getName();
		formal.setNodeType(formalType);
		if (!table.IsDeclaredInCurrentScope(formalId))
		{
			table.InsertNewDecleration(formalId, formalType);
		}
		else
		{
			throw new RuntimeException(formal.getLine()+": formal "+formalId+" is declered more than once.");
		}
	}
	
	public SymbolType visit(ClassMethod method, Environment env)
	{
		visitMethod(method, env);
		return null;
	}
	public SymbolType visit(StaticMethod method, Environment env)
	{
		if(!env.symbolTable.IsDeclaredInCurrentScope(method.getName()))
		{
			throw new RuntimeException("static method: " + method.getName()+ " has been declared more than once.");
		}
		else
		{
			env.symbolTable.StartScope();
			SymbolType methodType = type_t.getTypeFromAST(method.getType());
			env.symbolTable.InsertNewDecleration(method.getName(), methodType);
			for (MethodFormal formal : method.getFormals())
			{
				SymbolType formalType = formal.accept(this, env);
				env.symbolTable.InsertNewDecleration(formal.getName(), formalType);
			}
			
			for (Stmt s : method.getStatements().statements)
			{
				SymbolType stmtSymbol = s.accept(this, env);
				if ( (s != null) && (s.getClass() == LocalVar.class) || (s.getClass() == BlockStmt.class))
					env.symbolTable.InsertNewDecleration(s.toString(), stmtSymbol);
			}
			method.getType().accept(this, env);
			env.symbolTable.ExitScope();
			return methodType;
			
		}
	}
	public SymbolType visit(VirtualMethod method, Environment env)
	{
		if(!env.symbolTable.IsDeclaredInCurrentScope(method.getName()))
		{
			throw new RuntimeException("static method: " + method.getName()+ " has been declared more than once.");
		}
		else
		{
			env.symbolTable.StartScope();
			SymbolType methodType = type_t.getTypeFromAST(method.getType());
			env.symbolTable.InsertNewDecleration(method.getName(), methodType);
			for (MethodFormal formal : method.getFormals())
			{
				SymbolType formalType = formal.accept(this, env);
				env.symbolTable.InsertNewDecleration(formal.getName(), formalType);
			}
			
			for (Stmt s : method.getStatements().statements)
			{
				SymbolType stmtSymbol = s.accept(this, env);
				if ( (s != null) && (s.getClass() == LocalVar.class) || (s.getClass() == BlockStmt.class))
					env.symbolTable.InsertNewDecleration(s.toString(), stmtSymbol);
			}
			method.getType().accept(this, env);
			env.symbolTable.ExitScope();
			return methodType;
			
		}
	}
	
	public SymbolType visit(BlockStmt blk, Environment env)
	{
		table.StartScope();
		
		for (Stmt stmnt : blk.m_stmtList.statements)
		{
			stmnt.accept(this, env);
		}
		
		table.ExitScope();
	}
	public SymbolType visit(CallStmt call, Environment env)
	{
		call.getCall().accept(this, env);
	}
	
	public SymbolType visit(If stmt, Environment env) 
	{
		SymbolType sym_type = stmt.getCondition().accept(this, env);
		if(!sym_type.isBoolType())
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid expression inside if, condition is not of boolean type.");
		}
		
		table.StartScope();
		stmt.getOperation().accept(this, env);
		table.ExitScope();

		// Validate "else" statement
		if (stmt.hasElse()) 
		{
			table.StartScope();
			stmt.getElseOperation().accept(this, env);
			table.ExitScope();
		}
	}
	
	public SymbolType visit(While stmt, Environment env) 
	{
		stmt.getCondition().accept(this,env);
		env.loop_counter += 1;
		table.StartScope();
		stmt.getOperation().accept(this, env);
		table.ExitScope();
		env.loop_counter -= 1;
	}
	
	public SymbolType visit(Break stmt, Environment env)
	{
		if (env.loop_counter == 0)
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid  usage of a break statement not inside of loop");
		}
		return null;
	}

	public SymbolType visit(Continue stmt,  Environment env)
	{
		if (env.loop_counter == 0)
		{
			throw new RuntimeException(stmt.getLine()+
					": Invalid  usage of a continue statement not inside of loop");
		}
		return null;
	}
	
	public SymbolType (StaticFunctionCall statCall, Environment env)
	{
		
	}
	
	private void visitMethod(ClassMethod method, Environment env) 
	{
		String methodID = method.getName();

		for (MethodFormal formal : method.getFormals()) 
		{
			String formalId = formal.getName();
			symbolTypes.SymbolType formalType = type_t.getTypeFromAST(formal.getType());
			formal.setNodeType(formalType);
			
			formal.accept(this, env);
		}

		for (Stmt stmnt : method.statements.statements)
		{
			stmnt.accept(this, env);
		}
	}
}