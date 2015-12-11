package slp;

import java.util.List;

public class VirtualMethod extends ClassMethod {

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public VirtualMethod(Type type, String name, List<MethodFormal> formals, StmtList statements, int line) 
	{
		super(type, name, formals, statements, line);
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
