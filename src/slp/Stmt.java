package slp;

/** The super class of all AST node for program statements.
 */
public abstract class Stmt extends ASTNode {
	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
	String comment;
	protected Stmt(int line, String stmtComment)
	{
		super(line);
		this.comment = stmtComment;
	}
}