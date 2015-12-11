package slp;

public class MethodFormal extends ASTNode 
{
	private Type type;
	private String name;

	public void accept(Visitor visitor) 
	{
		visitor.visit(this);
	}

	public MethodFormal(Type type, String name, int line)
	{
		super(line);
		this.type = type;
		this.name = name;
	}

	public Type getType() 
	{
		return type;
	}

	public String getName() 
	{
		return name;
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
