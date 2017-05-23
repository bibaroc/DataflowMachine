import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentCompute {

	// Scanner to read from file.
	private static Scanner SC = null;
	// Left son's index: 2i+1
	// Right son's index: 2i+2
	// Father's index: floor((i-1)/2)
	private static HashMap<Integer, Object> tree;
	private static ConcurrentLinkedQueue<Integer> toResolve = null;

	// The builder imports the file in memory all by itself.
	ConcurrentCompute(String pathToExpression) {
		try {
			// Get the scanner and the hashMap
			SC = new Scanner(new File(pathToExpression));
			tree = new HashMap<>(2500000);
			toResolve = new ConcurrentLinkedQueue<Integer>();
			// If it is either an empty file or it starts with a double throw
			// IllegalStateException.
			if (SC.hasNextDouble() || !SC.hasNext()) {
				SC.close();
				throw new IllegalStateException("Sorry seems like the file is misconstructed");
			} else
				// If it seems ok, start populating the "Tree"
				populateTree(0);
			SC.close();

			System.out.print("Just finished reading the file.\r\n");
		} catch (FileNotFoundException e) {
			System.err.print("Unable to locate the file containing the expression.\r\n");
			System.exit(1);
		}
	}

	private void populateTree(int currentIndex) {
		boolean computable = false;
		// I know it is a string
		tree.put(currentIndex, SC.next());
		// If the first operand is a double i put it in place
		if (SC.hasNextDouble()) {
			computable = true;
			tree.put(2 * currentIndex + 1, SC.nextDouble());
		}
		// Or if it's a string, i can build a sub tree to mark that expression.
		else if (SC.hasNext())
			populateTree(2 * currentIndex + 1);
		// If there is no operand, the file is misconstructed.
		else
			throw new IllegalStateException("Sorry seems like the file is misconstructed");
		// Same for the second operand
		if (SC.hasNextDouble()) {
			if (computable)
				toResolve.add(2 * currentIndex + 2);
			tree.put(2 * currentIndex + 2, SC.nextDouble());
		} else if (SC.hasNext())
			populateTree(2 * currentIndex + 2);
		else
			throw new IllegalStateException("Sorry seems like the file is misconstructed");
	}

	public void resolve(int threadNumber) throws InterruptedException {
		Thread fff = new computingThread();
		fff.start();
		fff.join();
		System.out.print("Result: " + tree.get(0) + "\r\n");

	}

	public static void main(String[] args) throws InterruptedException {

		long start = System.currentTimeMillis();
		ConcurrentCompute cc = new ConcurrentCompute(
				"C:/Users/Bibaroc/Desktop/Workspace/DataFlowMachine/expression.txt");
		cc.resolve(0);
		System.out.print("Completed after: " + (System.currentTimeMillis() - start));

	}

	private class computingThread extends Thread {

		public void run() {

			while (!toResolve.isEmpty()) {

				int sonToCompute = toResolve.poll();
				computeThatRecursively(sonToCompute);
				sonToCompute = -1;
			}

		}

		private void computeThatRecursively(int sonToCompute) {
			int theOtherOne = ((sonToCompute & 1) == 0) ? sonToCompute - 1 : sonToCompute + 1;
			Object op0 = null;
			Object op1 = null;
			if ((op0 = tree.get(theOtherOne)) instanceof Double && (op1 = tree.get(sonToCompute)) instanceof Double) {
				int fatherIndex = Math.floorDiv(sonToCompute - 1, 2);
				String father = (String) tree.get(fatherIndex);
				Double result;
				if (father.equals("+"))
					result = (double) op0 + (double) op1;
				else if (father.equals("-"))
					result = (double) op0 - (double) op1;
				else if (father.equals("*"))
					result = (double) op0 * (double) op1;
				else if (father.equals("/"))
					result = (double) op0 / (double) op1;
				else
					throw new IllegalStateException("Sorry seems like the file is misconstructed");
				tree.put(fatherIndex, result);
				computeThatRecursively(fatherIndex);
			}

		}

	}
}
