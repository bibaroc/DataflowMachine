import java.io.*;
import java.util.*;

public class ConcurrentCompute {
	private String buffer;
	private long startTime;
	private static StringTokenizer st = null;
	private Tree root = null;

	ConcurrentCompute(String pathToExp) throws IOException {
		startTime = System.currentTimeMillis();
		// Try with resources, it's auto-closing.
		try (BufferedReader br = new BufferedReader(new FileReader(pathToExp))) {
			// Using this to buffer the file from hdd to ram.
			String bf = null;
			// After the loop, the global variable buffer should contain the
			// whole file.
			while ((bf = br.readLine()) != null)
				buffer += bf;
			// StringTokenizer is faster than Scanner.
			st = new StringTokenizer(buffer);
			System.out.print("String has: " + st.countTokens() + " tokens.\r\n");
			long endOfInput = System.currentTimeMillis();
			System.out.print("Reading the file took: " + (endOfInput - startTime) + " milliseconds.\r\n");
			//It's faster to skip conditioning and just throw an Exception if this fails.
			try {
				String currentToken = st.nextToken();
				//If the first token can be parsed into a double, the input string is misconstructed.
				try {
					Double.parseDouble(currentToken);
					throw new InvalidObjectException(
							"Sorry, but it seems like the first token of the file is a double and I can only process prefix notation.");
				} catch (NumberFormatException e) {
					root = new Tree(currentToken);
				}
			}
			// If there is no initial token the file is empty.
			catch (NoSuchElementException e) {
				throw new NoSuchElementException("The file appears to be empty.");
			}
			long buildTime = System.currentTimeMillis();
			System.out.print("Building took: " + (buildTime - endOfInput) + " milliseconds.\r\n");
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Sorry, seems like the file is missing.");
		} catch (IOException e) {
			throw new IOException("Sorry, apparently the file is already in use by another application.");
		}
	}

	public static void main(String[] args) throws IOException {
		ConcurrentCompute cc = new ConcurrentCompute("expression.txt");

	}

	/**
	 * Used to build the tree out of a lot of little and modular subTrees,
	 * 
	 * @author Vladyslav Sulimovskyy
	 *
	 */
	public class Tree {
		private double leftOperand, rightOperand, result;
		// If null, this is the root node.
		private Tree father = null;
		private String operation = null;

		// A sub tree is both the right and the left operands are realNumbers
		// and not dummies.
		public boolean computable() {
			return leftOperand != 0.0d && rightOperand != 0.0d;
		}

		// Constructs the root subTree
		Tree(String operation) {
			this.operation = operation;
			String ss = null;
			try {
				ss = st.nextToken();
				leftOperand = Double.parseDouble(ss);
			} catch (NumberFormatException e1) {
				leftOperand = (new Tree(this, ss)).result;
			} catch (NoSuchElementException e0) {
				throw new IllegalStateException("Semms like the file is misconstructed.");
			}
			try {
				ss = st.nextToken();
				rightOperand = Double.parseDouble(ss);
			} catch (NumberFormatException e1) {
				rightOperand = (new Tree(this, ss)).result;
			} catch (NoSuchElementException e0) {
				throw new IllegalStateException("Semms like the file is misconstructed.");
			}
		}

		/**
		 * Constructs a subTree given father and operation to perform.
		 * 
		 * @param father
		 *            The subTree father of this one.
		 * @param operation
		 *            The operation to perform.
		 */
		Tree(Tree father, String operation) {
			this.father = father;
			this.operation = operation;
			String ss = null;

			try {
				ss = st.nextToken();
				leftOperand = Double.parseDouble(ss);
			} catch (NumberFormatException e1) {
				leftOperand = (new Tree(this, ss)).result;
			} catch (NoSuchElementException e0) {
				throw new IllegalStateException("Semms like the file is misconstructed.");
			}
			try {
				ss = st.nextToken();
				rightOperand = Double.parseDouble(ss);
			} catch (NumberFormatException e1) {
				rightOperand = (new Tree(this, ss)).result;
			} catch (NoSuchElementException e0) {
				throw new IllegalStateException("Semms like the file is misconstructed.");
			}
		}
	}
}
