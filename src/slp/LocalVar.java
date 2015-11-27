package slp;

/** An AST node for program variables.
 */
public class LocalVar extends Stmt {
	public  String name;
	private Type t;
	private Expr initValue = null;
	public LocalVar(Type t,String name){
		this.t  = t;
		this.name=name;
	}
	public LocalVar(Type t,String name, Expr initValue){
		this(t,name);
		this.initValue=initValue;
	}
	public Type getType(){
		return t;
	}
	public String getName(){
		return name;
	}
	public boolean hasInitValue(){
		return (initValue!=null);
	}
	public Expr getInitValue(){
		return initValue;
	}
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
		
	}

	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this,context);
	}
	
	
}