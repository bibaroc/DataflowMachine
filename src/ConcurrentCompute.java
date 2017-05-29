import java.io.*;
import java.util.*;

public class ConcurrentCompute {

	private static String buffer = "";
	private static StringTokenizer st = null;
	private static LinkedList<Tree> ll = null;
	private static Tree root;

	ConcurrentCompute(String pathToExp) throws IOException, InterruptedException {
		long startTime = System.currentTimeMillis();
		// Try with resources, it's auto-closing.
		try (BufferedReader br = new BufferedReader(new FileReader(pathToExp))) {
			// Using this to buffer the file from hdd to ram.
			String bf;
			// After the loop, the global variable buffer should contain the
			// whole file.
			while ((bf = br.readLine()) != null)
				buffer += bf;
			// StringTokenizer is faster than Scanner.
			st = new StringTokenizer(buffer);
			//System.out.println(buffer);
			// It's faster to skip conditioning and just throw an Exception if
			// this fails.
			try {
				bf = st.nextToken();
				// If the first token can be parsed into a double, the input
				// string is misconstructed.
				Double.parseDouble(bf);
			}
			// If i cannot parse a double from the first element, it's good to
			// go.
			catch (NumberFormatException e) {
				ll = new LinkedList<Tree>();
				root = new Tree(null, bf);
			}
			// If there is no initial token the file is empty.
			catch (NoSuchElementException e) {
				throw new NoSuchElementException("The file appears to be empty.");
			}
			//For now testing with just a thread.
			Thread main = new Resolver();
			main.start();
			main.join();
			long buildTime = System.currentTimeMillis();
			System.out.print("Building took: " + (buildTime - startTime) + " milliseconds.\r\n");
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Sorry, seems like the file is missing.");
		} catch (IOException e) {
			throw new IOException("Sorry, apparently the file is already in use by another application.");
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		ConcurrentCompute cc = new ConcurrentCompute("expression.txt");

	}

	/**
	 * Used to climb the tree bottom to top each time solving what possible.
	 * @author Vladyslav Sulimovsky
	 *
	 */
	public class Resolver extends Thread {
		public void run() {
			try {
				checkForSolvables();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void checkForSolvables() throws InterruptedException {
			synchronized (ll) {
				if (ll.isEmpty())
					// wait();
					System.out.println("Result: " + root.result);
				else
					operate(ll.removeLast());
			}
		}

		private void operate(Tree removeLast) throws InterruptedException {
			String op = removeLast.operation;
			switch (op) {
			case "+":
				removeLast.result = removeLast.leftSon.result + removeLast.rightSon.result;
				break;
			case "-":
				removeLast.result = removeLast.leftSon.result - removeLast.rightSon.result;
				break;
			case "*":
				removeLast.result = removeLast.leftSon.result * removeLast.rightSon.result;
				break;
			case "/":
				removeLast.result = removeLast.leftSon.result / removeLast.rightSon.result;
				break;
			}
			//System.out.println(removeLast.result + " = " + removeLast.leftSon.result + " " + removeLast.operation + " "
			//		+ removeLast.rightSon.result);
			if (removeLast.father!=null&&removeLast.father.computable()) {
				ll.add(removeLast.father);
				checkForSolvables();
			} else
			checkForSolvables();

		}
	}

	public class Tree {
		private Tree father;
		private Tree rightSon, leftSon;
		private String operation;
		private double result;

		public boolean computable() {
			return rightSon.result != 0.0d && leftSon.result != 0.0d;

		}

		Tree(Tree father, String oper) {
			boolean comp = false;
			this.father = father;
			operation = oper;
			String ss = null;
			try {
				ss = st.nextToken();
				leftSon = new Tree(Double.parseDouble(ss));
				comp = true;
			} catch (NumberFormatException e0) {
				leftSon = new Tree(this, ss);
			} catch (NoSuchElementException e0) {
				System.err.println("Stringa malformata");
			}
			try {
				ss = st.nextToken();
				rightSon = new Tree(Double.parseDouble(ss));
				synchronized (ll) {
					if (comp) {
						ll.add(this);
						ll.notify();
					}
				}
			} catch (NumberFormatException e0) {
				rightSon = new Tree(this, ss);
			} catch (NoSuchElementException e0) {
				System.err.println("Stringa malformata");
			}
		}

		Tree(double value) {
			this.result = value;
		}
	}
}
