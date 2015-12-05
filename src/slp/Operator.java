package slp;

/** An enumeration containing all the operation types in the SLP language.
 */
public enum Operator {
	MINUS, PLUS, MULT, DIV, MOD, LT, GT, LE, GE, LAND, LOR, EQUAL, NEQUAL, LNEG;
	
	/** Prints the operator in the same way it appears in the program.
	 */
	public String toString() {
		switch (this) {
		case MINUS: return "-";
		case PLUS: return "+";
		case MULT: return "*";
		case DIV: return "/";
		case MOD: return "%";
		case LT: return "<";
		case GT: return ">";
		case LE: return "<=";
		case GE: return ">=";
		case LAND: return "&&";
		case LOR: return "||";
		case EQUAL: return "==";
		case NEQUAL: return "!=";
		case LNEG: return "!";
		default: throw new RuntimeException("Unexpted value: " + this.name());
		}
	}
}