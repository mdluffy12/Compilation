package slp;

public class If extends Stmt{
	private Expr cond;
	private Stmt operation;
	private Stmt elseOp = null;
	
	public If(Expr cond,Stmt operation,Stmt elseOp, int line,String comment){
		this(cond, operation, line, comment);
		this.elseOp=elseOp;
	}
	public If(Expr cond, Stmt op, int line, String comment){
		super(line,comment);
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
	public void accept(Visitor visitor) {
		visitor.visit(this);

	}
	
	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return visitor.visit(this, context);
	}
	
}
