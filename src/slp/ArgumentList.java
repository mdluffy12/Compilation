package slp;

public class ArgumentList extends ASTNode {

	public ArgumentList next;
	public Expr data;
	
	public ArgumentList(Expr data, ArgumentList next, int line)
	{
		super(line);
		this.next = next;
		this.data = data;
	}
	
	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub
		visitor.visit(this);
	}

	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return null;
	}

}
