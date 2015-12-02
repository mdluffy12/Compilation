package slp;

import java.util.List;

public abstract class ClassMethod extends ClassMember {

	protected Type type;
	protected String name;
	protected List<MethodFormal> formals;
	protected StmtList statements;

	protected ClassMethod(Type type, String name, List<MethodFormal> formals, StmtList statements)
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

	public StmtList getStatements() 
	{
		return statements;
	}
	
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	/** Accepts a propagating visitor parameterized by two types.
	 * 
	 * @param <DownType> The type of the object holding the context.
	 * @param <UpType> The type of the result object.
	 * @param visitor A propagating visitor.
	 * @param context An object holding context information.
	 * @return The result of visiting this node.
	 */
	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}	
}