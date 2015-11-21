package src.slp;

public class CallStmt extends Stmt {
	private Call call;
	public CallStmt(Call c){
		this.call=c;
	}
	public Call getCall(){
		return call;
	}
	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}

}
