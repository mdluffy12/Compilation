package src.slp;

public class Return extends Stmt {

	private Expr value= null;
	
	public Return(Expr e){
		this.value=e;
	}
	public boolean hasValue(){
		return (value!=null);
	}
	public Expr getValue(){
		return value;
	}
	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this,context);
	}

}
