package slp;

public class ArgumentList extends ASTNode {

	public ArgumentList next;
	public Expr data;
	
	public ArgumentList(Expr data, ArgumentList next)
	{
		this.next = next;
		this.data = data;
	}
	
	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return null;
	}

}
