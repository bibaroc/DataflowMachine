import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ConcurrentCompute {

	private static Scanner SC = null;
	// Left son's index: 2i+1
	// Right son's index: 2i+2
	// Father's index: floor((i-1)/2)
	private static ArrayList<Object> tree = null;

	ConcurrentCompute(String pathToExpression) {
		try {
			SC = new Scanner(new BufferedInputStream(new FileInputStream(pathToExpression)));
			SC.useDelimiter(" ");
			if (SC.hasNextDouble() || !SC.hasNext())
				throw new IllegalStateException("Sorry seems like the file is misconstructed");
			tree = new ArrayList<Object>(4000000);
			tree.add(null);
			populateTree(0);
			SC.close();
			System.out.print("Just finished reading the file.\r\n");
		} catch (FileNotFoundException e) {
			System.err.print("Unable to locate the file containing the expression.\r\n");
			System.exit(1);
		}
	}

	private void populateTree(int currentIndex) {
		System.out.print(tree.size() + "\r\n");
		// I know it is a string
		tree.set(currentIndex, SC.next());
		enlargeTreeUpTo(2 * currentIndex + 2);
		// if the char following the operator exists
		if (SC.hasNext())
			// And it is a double
			if (SC.hasNextDouble())
				tree.set(2 * currentIndex + 1, SC.nextDouble());
			// Else it is a String
			else
				populateTree(2 * currentIndex + 1);
		// If after an operator there are no operands
		else
			throw new IllegalStateException("Sorry seems like the file is misconstructed");
		// Looking for the second element
		if (SC.hasNext())
			// If it's a double
			if (SC.hasNextDouble())
				tree.set(2 * currentIndex + 2, SC.nextDouble());
			// Or maybe a String
			else
				populateTree(2 * currentIndex + 2);
		// If there is no second element
		else
			throw new IllegalStateException("Sorry seems like the file is misconstructed");
		// TODO Auto-generated method stub

	}

	private void enlargeTreeUpTo(int futureSize) {
		for (int i = tree.size(); i <= futureSize; i++)
			tree.add(null);
	}

	public static void main(String[] args) {
		ConcurrentCompute cc = new ConcurrentCompute("C:/Users/Bibaroc/Desktop/expression.txt");
		for (int i = 0; i < tree.size(); i++)
			System.out.print(tree.get(i) + " ");
	}

}
