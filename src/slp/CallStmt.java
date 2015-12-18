package slp;

public class CallStmt extends Stmt {
	private FunctionCall call;
	public CallStmt(FunctionCall c, int line, String comment){
		super(line, comment);
		this.call=c;
	}
	public FunctionCall getCall(){
		return call;
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
