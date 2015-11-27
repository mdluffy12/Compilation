package slp;

public class BoolLiteral extends Literal {
		public boolean value;
		public BoolLiteral(boolean bValue)
		{
			this.value = bValue;
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <DownType, UpType> UpType accept(
				PropagatingVisitor<DownType, UpType> visitor, DownType context) {
			return visitor.visit(this, context);
		}	
}
