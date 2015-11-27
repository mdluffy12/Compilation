package slp;

/**
 * Class ClassField <= ASTNode.
 */
public class ClassField extends ClassMember {

	private Type fieldType;
	private String fieldName;

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public ClassField(Type fieldType, String fieldName) {
		this.fieldType = fieldType;
		this.fieldName = fieldName;
	}

	public Type getType() {
		return fieldType;
	}

	public String getName() {
		return fieldName;
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
