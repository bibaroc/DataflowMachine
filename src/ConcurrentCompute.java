import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class ConcurrentCompute {

	// Scanner to read from file.
	private static Scanner SC = null;
	// Left son's index: 2i+1
	// Right son's index: 2i+2
	// Father's index: floor((i-1)/2)
	private static HashMap<Integer, Object> tree;

	// The builder imports the file in memory all by itself.
	ConcurrentCompute(String pathToExpression) {
		try {
			// Get the scanner and the hashMap
			SC = new Scanner(new File(pathToExpression));
			tree = new HashMap<>();
			// If it is either an epty file or it starts with a double throw
			// IllegalStateException.
			if (SC.hasNextDouble() || !SC.hasNext()) {
				SC.close();
				throw new IllegalStateException("Sorry seems like the file is misconstructed");
			} else
				// If it seems ok, start polulating the "Tree"
				populateTree(0);
			SC.close();
			System.out.print("Just finished reading the file.\r\n");
		} catch (FileNotFoundException e) {
			System.err.print("Unable to locate the file containing the expression.\r\n");
			System.exit(1);
		}
	}

	private void populateTree(int currentIndex) {
		// I know it is a string
		tree.put(currentIndex, SC.next());
		// If the first operand is a double i put it in place
		if (SC.hasNextDouble())
			tree.put(2 * currentIndex + 1, SC.nextDouble());
		// Or if it's a string, i can build a sub tree to mark that expression.
		else if (SC.hasNext())
			populateTree(2 * currentIndex + 1);
		// If there is no operand, the file is misconstructed.
		else
			throw new IllegalStateException("Sorry seems like the file is misconstructed");
		// Same for the second operand
		if (SC.hasNextDouble())
			tree.put(2 * currentIndex + 2, SC.nextDouble());
		else if (SC.hasNext())
			populateTree(2 * currentIndex + 2);
		else
			throw new IllegalStateException("Sorry seems like the file is misconstructed");
	}

	public static void main(String[] args) {
		ConcurrentCompute cc = new ConcurrentCompute(
				"C:/Users/Bibaroc/Desktop/Workspace/DataFlowMachine/expression.txt");
	}

}
