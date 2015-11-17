package slp;

import java.util.List;

public class ICClass extends ASTNode 
{
	private String name;
	private String superClassName = null;
	private List<ClassField> fields;
	private List<ClassMethod> methods;

	public void accept(Visitor visitor)
	{
		visitor.visit(this);
	}

	public ICClass(String name, List<ClassField> fields, List<ClassMethod> methods)
	{
		this.name = name;
		this.fields = fields;
		this.methods = methods;
	}

	public ICClass(String name, String superClassName, List<ClassField> fields, List<ClassMethod> methods)
	{
		this(name, fields, methods);
		this.superClassName = superClassName;
	}

	public String getName() 
	{
		return name;
	}

	public boolean hasSuperClass() 
	{
		return (superClassName != null);
	}

	public String getSuperClassName() 
	{
		return superClassName;
	}

	public List<ClassField> getFields()
	{
		return fields;
	}

	public List<ClassMethod> getMethods()
	{
		return methods;
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
