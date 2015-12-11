package slp;

public abstract class Type extends ASTNode {

	/**
	 * Number of array 'dimensions' in data type.
	 * For example:
	 * 		int[][] -> dim = 2.
	 * 		int[] -> dim = 1.
	 * 		int -> dim = 0.
	 */
	private int dim = 0;

	protected Type(int line) 
	{
		super(line);
	}

	public abstract String getName();

	public int getDimension() {
		return dim;
	}

	public void incrementDimension() {
		++dim;
	}
}