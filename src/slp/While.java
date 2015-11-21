package src.slp;

public class While extends Stmt {
	private Expr cond;
	private Stmt op;
	public While(Expr cond,Stmt op){
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
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return visitor.visit(this, context);
	}

}
