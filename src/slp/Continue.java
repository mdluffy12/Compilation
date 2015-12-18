package slp;

public class Continue extends Stmt {
	public Continue(int line, String comment){
		super(line, comment);
	}
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
		
	}
	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}

}
