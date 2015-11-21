package src.slp;
import java.util.List;

public class Call extends Expr {
	private String name;
	private List<Expr> arguments;
	protected Call(String name,List<Expr> args){
		this.name=name;
		this.arguments=args;
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
