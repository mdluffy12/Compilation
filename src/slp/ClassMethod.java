package slp;

import java.util.List;

public abstract class ClassMethod extends ASTNode {

	protected Type type;
	protected String name;
	protected List<MethodFormal> formals;
	protected List<Stmt> statements;

	protected ClassMethod(Type type, String name, List<MethodFormal> formals, List<Stmt> statements)
	{
		this.type = type;
		this.name = name;
		this.formals = formals;
		this.statements = statements;
	}

	public Type getType() 
	{
		return type;
	}

	public String getName() 
	{
		return name;
	}

	public List<MethodFormal> getFormals() 
	{
		return formals;
	}

	public List<Stmt> getStatements() 
	{
		return statements;
	}
}