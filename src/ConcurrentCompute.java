
import java.io.*;
import java.util.*;

/**
 * Concurrently computes a huge-ass polish notation expression. This class is up
 * for no good, please avoid or proceed at your own risk.
 * 
 * @author Vladyslav Sulimovsky
 *
 */
public class ConcurrentCompute {

	private String buffer = "";
	private StringTokenizer stringTokenz = null;
	private LinkedList<Tree> treesToProcess = null;
	private ArrayList<Resolver> workers;
	private Tree rootTree;
	private boolean finishedBuildingTree = false;
	private long startTime;

	ConcurrentCompute(String pathToExp) throws IOException, InterruptedException {
		startTime = System.currentTimeMillis();
		// Try with resources, it's auto-closing.
		try (BufferedReader br = new BufferedReader(new FileReader(pathToExp))) {
			// Using this to buffer the file from hdd to ram.
			String bf;
			// After the loop, the global variable buffer should contain the
			// whole file.
			while ((bf = br.readLine()) != null)
				buffer += bf;
			// StringTokenizer is faster than Scanner.
			stringTokenz = new StringTokenizer(buffer);
			// It's faster to skip conditioning and just throw an Exception if
			// this fails.
			try {
				bf = stringTokenz.nextToken();
				// If the first token can be parsed into a double, the input
				// string is misconstructed.
				Double.parseDouble(bf);
			}
			// If i cannot parse a double from the first element, it's good to
			// go.
			catch (NumberFormatException e) {
				treesToProcess = new LinkedList<Tree>();
				workers = new ArrayList<Resolver>();
				spawnWorkers();
				buildTree(bf);
				waitForWorkers();
			}
			// If there is no initial token the file is empty and nextToken()
			// fails.
			catch (NoSuchElementException e) {
				throw new NoSuchElementException("The file appears to be empty.");
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Sorry, seems like the file is missing.");
		} catch (IOException e) {
			throw new IOException("Sorry, apparently the file is already in use by another application.");
		}
	}

	private void waitForWorkers() {
		try {
			for (Resolver in : workers)
				in.join();
			long endTime = System.currentTimeMillis();
			System.out.println("Result was: " + rootTree.result);
			System.out.println("It took me: " + (endTime - startTime) + " milliseconds.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds the tree using the given String as root.
	 * 
	 * @param firstElement
	 *            The fist element of the polish notation, must be an operation.
	 */
	private void buildTree(String firstElement) {
		rootTree = new Tree(null, firstElement);
		long buildTime = System.currentTimeMillis();
		System.out.print("Building the tree took: " + (buildTime - startTime) + " milliseconds. \r\n");
		finishedBuildingTree = true;
		// Notifying all the threads that could be waiting for work.
		synchronized (treesToProcess) {
			treesToProcess.notifyAll();
		}
	}

	/**
	 * Spawns some workers to operate on the expression tree. The total number
	 * of workers equals the number of available logical cores.
	 */
	private void spawnWorkers() {
		int numbCores = 1;
		System.out.println("Spawning: " + numbCores + " threads.");
		for (int i = numbCores; i > 0; i--)
			workers.add(new Resolver());
		for (Resolver in : workers)
			in.start();
	}

	/**
	 * Used to climb the tree bottom to top each time solving what possible.
	 * 
	 * @author Vladyslav Sulimovsky
	 *
	 */
	public class Resolver extends Thread {
		/**
		 * When spawned it checks for available subTrees to compute.
		 */
		public void run() {
			try {
				checkForSolvables();
			} catch (InterruptedException e) {
				System.err.println("You shall not interrupt me u dumb fuck.");
				System.exit(420);
				// Blaze it
			}
		}

		/**
		 * Checks for the items available to be solved, if none waits to be
		 * notified or even closes itself if the tree is completely built.
		 * 
		 * @throws InterruptedException
		 *             If you interrupt it. Don't.
		 */
		private synchronized void checkForSolvables() throws InterruptedException {
			synchronized (treesToProcess) {
				do {
					treesToProcess.wait();
					if (finishedBuildingTree)
						return;
				} while (treesToProcess.isEmpty());
				// Not gonna fail, inside the synchronized block.
				operate(treesToProcess.removeFirst());
				checkForSolvables();
			}
		}

		/**
		 * Computes the value of the expression given the subTree and checks if
		 * the father of this expression can be computed.
		 * 
		 * @param subTree
		 *            The subTree i want to operate on.
		 * @throws InterruptedException
		 *             If you interrupt dis.
		 */
		private void operate(Tree subTree) throws InterruptedException {
			synchronized (subTree) {
				String op = subTree.operation;
				// if(subTree.leftSon.result==Double.NaN||subTree.rightSon.result!=Double.NaN)
				// System.out.println("error");
				switch (op) {
				case "+":
					subTree.result = subTree.leftSon.result + subTree.rightSon.result;
					System.out.println(subTree.result + " = " + subTree.leftSon.result + " " + subTree.operation + " "
							+ subTree.rightSon.result);
					break;
				case "-":
					subTree.result = subTree.leftSon.result - subTree.rightSon.result;
					System.out.println(subTree.result + " = " + subTree.leftSon.result + " " + subTree.operation + " "
							+ subTree.rightSon.result);
					break;
				case "*":
					subTree.result = subTree.leftSon.result * subTree.rightSon.result;
					System.out.println(subTree.result + " = " + subTree.leftSon.result + " " + subTree.operation + " "
							+ subTree.rightSon.result);
					break;
				case "/":
					subTree.result = subTree.leftSon.result / subTree.rightSon.result;
					System.out.println(subTree.result + " = " + subTree.leftSon.result + " " + subTree.operation + " "
							+ subTree.rightSon.result);
					break;
				default:
					throw new IllegalArgumentException(op + " is not a valid operation sign.");
				}
				if (subTree.father != null && subTree.father.computable())
					operate(subTree.father);
			}

		}
	}

	/**
	 * Class used to build the tree. Base implementation of an unbalanced binary
	 * tree.
	 * 
	 * @author Vladyslav Sulimovsky
	 *
	 */
	public class Tree {
		private Tree father, rightSon, leftSon = null;
		private String operation = "";
		private double result;

		/**
		 * To be computable it must have both the sons and the values of both
		 * must be numbers.
		 * 
		 * @return Is this is computable or not.
		 */
		public boolean computable() {
			// Fuck you
			return (rightSon != null ? leftSon.result != Double.NaN && rightSon.result != Double.NaN : false);
		}

		Tree(Tree fatherSubTree, String operator) {
			result = Double.NaN;
			this.father = fatherSubTree;
			operation = operator;
			String ss = null;
			try {
				ss = stringTokenz.nextToken();
				leftSon = new Tree(this, Double.parseDouble(ss));
			}
			// If the element following the operation cannot be parsed into a
			// double, it must be an operation.
			catch (NumberFormatException e0) {
				leftSon = new Tree(this, ss);
			}
			// If there are no elements to be taken the input is malformed.
			catch (NoSuchElementException e1) {
				System.err.println("Malformed input i'm sorry.");
				System.exit(123);
			}
			// Same shit
			try {
				ss = stringTokenz.nextToken();
				double res = Double.parseDouble(ss);
				rightSon = new Tree(this, res);
				// If the first element is a number and this also is, the tree
				// is computable and is pushed into the list.
				synchronized (treesToProcess) {
					if (leftSon.result != Double.NaN) {
						treesToProcess.addLast(this);
						treesToProcess.notify();
					}
				}
			} catch (NumberFormatException e0) {
				rightSon = new Tree(this, ss);
			} catch (NoSuchElementException e1) {
				System.err.println("Malformed input i'm sorry.");
				System.exit(123);
			}
		}

		Tree(Tree fatherSubTree, double value) {
			result = value;
			father = fatherSubTree;
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		ConcurrentCompute cc = new ConcurrentCompute("expression.txt");
	}
}
