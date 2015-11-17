package slp;

import java.util.List;

public class Program extends ASTNode 
{

	private List<ICClass> classes;

	@Override
	public void accept(Visitor visitor)
	{
		visitor.visit(this);
	}

	public Program(List<ICClass> classes) 
	{
		this.classes = classes;
	}

	public List<ICClass> getClasses() 
	{
		return classes;
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
