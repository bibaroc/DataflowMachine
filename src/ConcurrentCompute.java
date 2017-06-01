import java.io.*;
import java.util.*;

/**
 * Cuncurrently computes a huge ass polish notation expression. Do not use, u
 * have been warned.
 * 
 * @author Vladyslav Sulimovsky
 *
 */
public class ConcurrentCompute {

	private static String buffer = "";
	private static StringTokenizer st = null;
	private static LinkedList<Tree> ll = null;
	private static Tree root;
	private static boolean finished = false;

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
			// System.out.println(buffer);
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
				spawnWorkers();
				buildTree(bf);
			}
			// If there is no initial token the file is empty.
			catch (NoSuchElementException e) {
				throw new NoSuchElementException("The file appears to be empty.");
			}
			// For now testing with just a thread.
			Thread main = new Resolver();
			main.start();
			main.join();
			long buildTime = System.currentTimeMillis();
			System.out.print("Result is:" + root.result + "\r\nBuilding took: " + (buildTime - startTime)
					+ " milliseconds.\r\n");
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Sorry, seems like the file is missing.");
		} catch (IOException e) {
			throw new IOException("Sorry, apparently the file is already in use by another application.");
		}
	}

	private void buildTree(String bf) {
		root = new Tree(null, bf);
		finished = true;
	}

	private void spawnWorkers() {
		int numbCores = Runtime.getRuntime().availableProcessors();
System.out.println("Spawning: "+numbCores+" threads.");
		for (int i = 0; i < numbCores; i++)
			(new Resolver()).start();
		synchronized (ll) {
			ll.notifyAll();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		ConcurrentCompute cc = new ConcurrentCompute("expression.txt");

	}

	/**
	 * Used to climb the tree bottom to top each time solving what possible.
	 * 
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
				while (ll.isEmpty())
					if (finished) {
						System.out.println("Ho finito il risultato e': " + root.result);
						return;
					} else
						ll.wait(100);
				operate(ll.removeLast());
				checkForSolvables();
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
			default:
				throw new IllegalArgumentException(op + " is not a valid operation sign.");
			}
			if (removeLast.father != null && removeLast.father.computable())
				operate(removeLast.father);
		}
	}

	public class Tree {
		private Tree father, rightSon, leftSon = null;
		private String operation = "";
		private double result;

		public boolean computable() {
			if (leftSon != null && rightSon != null)
				return rightSon.result != Double.NaN && leftSon.result != Double.NaN;
			else
				return false;

		}

		Tree(Tree father, String oper) {
			this.father = father;
			operation = oper;
			result = Double.NaN;
			String ss = null;
			boolean comp = false;
			try {
				ss = st.nextToken();
				leftSon = new Tree(this, Double.parseDouble(ss));
				comp = true;
			} catch (NumberFormatException e0) {
				leftSon = new Tree(this, ss);
			} catch (NoSuchElementException e0) {
				System.err.println("Stringa malformata");
			}
			try {
				ss = st.nextToken();
				rightSon = new Tree(this, Double.parseDouble(ss));
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

		Tree(Tree father, double value) {
			this.father = father;
			this.result = value;
		}
	}
}
