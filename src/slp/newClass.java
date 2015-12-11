package slp;

public class newClass extends Expr {
	public String classID;
	public newClass(String classID, int line)
	{
		super(line);
		this.classID = classID;
	}
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return visitor.visit(this, context);
	}

}
