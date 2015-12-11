package slp;

import java.util.List;

public class ICClass extends ASTNode 
{
	private String name;
	private String superClassName = null;
	private List<ClassMember> members;

	public ICClass(String name, List<ClassMember> members, int line)
	{
		super(line);
		this.name = name;
		this.members = members;
	}

	public ICClass(String name, String superClassName, List<ClassMember> members, int line)
	{
		this(name, members, line);
		this.superClassName = superClassName;
	}
	
	public void accept(Visitor visitor)
	{
		visitor.visit(this);
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

	public List<ClassMember> getMembers()
	{
		return members;
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
