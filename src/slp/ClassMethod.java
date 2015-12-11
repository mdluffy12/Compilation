package slp;

import java.util.List;

public abstract class ClassMethod extends ClassMember {

	protected Type type;
	protected String name;
	protected List<MethodFormal> formals;
	protected StmtList statements;

	protected ClassMethod(Type type, String name, List<MethodFormal> formals, StmtList statements, int line)
	{
		super(line);
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

	public StmtList getStatements() 
	{
		return statements;
	}
}