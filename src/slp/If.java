package src.slp;

public class If extends Stmt{
	private Expr cond;
	private Stmt operation;
	private Stmt elseOp = null;
	
	public If(Expr cond,Stmt operation,Stmt elseOp){
		this(cond,operation);
		this.elseOp=elseOp;
	}
	public If(Expr cond,Stmt op){
		this.cond=cond;
		this.operation=op;
	}
	public Expr getCondition(){
		return cond;
	}
	public Stmt getOperation(){
		return operation;
	}
	public Stmt getElseOperation(){
		return elseOp;
	}
	public boolean hasElse(){
		return (elseOp!=null);
	}
	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}
	
}
