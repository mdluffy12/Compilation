package slp;

public class While extends Stmt {
	private Expr cond;
	private Stmt op;
	public While(Expr cond,Stmt op, int line,String comment){
		super(line, comment);
		this.cond=cond;
		this.op=op;
	}
	public Expr getCondition(){
		return cond;
	}
	public Stmt getOperation(){
		return op;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);

	}
	
	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return visitor.visit(this, context);
	}

}
