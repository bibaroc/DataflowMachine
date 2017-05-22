import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class ConcurrentCompute {

	private static Scanner SC = null;
	// Left son's index: 2i+1
	// Right son's index: 2i+2
	// Father's index: floor((i-1)/2)
	private static HashMap<Integer, Object> tree;
	static int i = 0;

	ConcurrentCompute(String pathToExpression) {
		try {
			SC = new Scanner(new BufferedInputStream(new FileInputStream(pathToExpression)));
			SC.useDelimiter(" ");
			
			if (SC.hasNextDouble() || !SC.hasNext())
				throw new IllegalStateException("Sorry seems like the file is misconstructed");
			tree = new HashMap<>();
			//int i = 0;
			//String ob;
			//while(SC.hasNext()){
			//	i++;
			//	ob = SC.next();
			//	tree.put(ob.hashCode(), ob);}
			//System.out.print(i+"\r\n");
		//	System.exit(0);
			populateTree(0);
			SC.close();
			System.out.print("Just finished reading the file.\r\n");
		} catch (FileNotFoundException e) {
			System.err.print("Unable to locate the file containing the expression.\r\n");
			System.exit(1);
		}
	}

	private void populateTree(int currentIndex) {
		System.out.print(i + "\r\n");
		// I know it is a string
		i++;
		tree.put(currentIndex, SC.next());
		// if the char following the operator exists
		if (SC.hasNext())
			// And it is a double
			if (SC.hasNextDouble()){
				i++;
				tree.put(2 * currentIndex + 1, SC.nextDouble());}
			// Else it is a String
			else
				populateTree(2 * currentIndex + 1);
		// If after an operator there are no operands
		else
			throw new IllegalStateException("Sorry seems like the file is misconstructed");
		// Looking for the second element
		if (SC.hasNext())
			// If it's a double
			if (SC.hasNextDouble()){
				i++;
				tree.put(2 * currentIndex + 2, SC.nextDouble());}
			// Or maybe a String
			else
				populateTree(2 * currentIndex + 2);
		// If there is no second element
		else
			throw new IllegalStateException("Sorry seems like the file is misconstructed");
		}

	public static void main(String[] args) {
		ConcurrentCompute cc = new ConcurrentCompute("C:/expression.txt");
		for(Integer in: tree.keySet())
			System.out.print(tree.get(in)+" ");
	}

}
