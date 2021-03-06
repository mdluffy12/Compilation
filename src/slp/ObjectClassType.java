package slp;

public class ObjectClassType extends Type 
{

	private String name;

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public ObjectClassType(int line, String name) 
	{
		super(line);
		this.name = name;
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