
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Concurrently computes a huge-ass polish notation expression. This class is up
 * for no good, please avoid or proceed at your own risk.
 * 
 * @author Vladyslav Sulimovsky
 *
 */
public class ConcurrentCompute {
	private Tree rootTree;
	private long startTime;
	private StringTokenizer stringTokenz = null;
	private LinkedList<Resolver> workers = null;
	private boolean finishedBuildingTree = false;
	private LinkedList<Tree> treesToProcess = null;

	ConcurrentCompute(String pathToExp) throws IOException, InterruptedException {
		Thread.currentThread().setPriority(1);
		startTime = System.currentTimeMillis();
		// Try with resources, it's auto-closing.
		try (BufferedReader br = new BufferedReader(new FileReader(pathToExp))) {
			// Using this to buffer the file from hdd to ram.
			String buffer = "";
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
			// If i cannot parse a double from the first element it could be ok.
			catch (NumberFormatException e0) {
				treesToProcess = new LinkedList<Tree>();
				workers = new LinkedList<Resolver>();
				buildTree(bf);
				// for (Tree in : treesToProcess)
				// solve(in);
				spawnWorkers();
				waitForWorkers();
			}
			// If there is no initial token the file is empty and nextToken()
			// fails.
			catch (NoSuchElementException e1) {
				throw new NoSuchElementException("The file appears to be empty.");
			}
		} catch (FileNotFoundException e0) {
			throw new FileNotFoundException("Sorry, seems like the file is missing.");
		} catch (IOException e1) {
			throw new IOException("Sorry, apparently the file is already in use by another application.");
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
		int numbCores = Runtime.getRuntime().availableProcessors();
		System.out.println("Spawning: " + numbCores + " threads.");
		for (int i = numbCores; i > 0; i--)
			workers.add(new Resolver());
		for (Resolver in : workers)
			in.start();
	}
	
	/**
	 * Just waits for workers and eventualy when everyone has finished pulls the
	 * result.
	 */
	private void waitForWorkers() {
		try {
			for (Resolver in : workers)
				in.join();
			long endTime = System.currentTimeMillis();
			System.out.println("Result was: " + rootTree.result);
			System.out.println("It took me: " + (endTime - startTime) + " milliseconds.");
		} catch (InterruptedException e0) {
			System.err.println("Dont interrupt me you sick nerd.");
			System.exit(123);
		}
	}

	/**
	 * Used to check for correctness.
	 * 
	 * @param subTree
	 */
	private void solve(Tree subTree) {
		switch (subTree.operation) {
		case "+":
			subTree.result = subTree.leftSon.result + subTree.rightSon.result;
			break;
		case "-":
			subTree.result = subTree.leftSon.result - subTree.rightSon.result;
			break;
		case "*":
			subTree.result = subTree.leftSon.result * subTree.rightSon.result;
			break;
		case "/":
			subTree.result = subTree.leftSon.result / subTree.rightSon.result;
			break;
		default:
			throw new IllegalArgumentException(subTree.operation + " is not a valid operation sign.");
		}
		if (subTree.father != null && subTree.father.isComputable())
			solve(subTree.father);
		else if (subTree.father == null)
			System.out.println("Il risultato e': " + rootTree.result);
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
			synchronized (treesToProcess) {
				try {
					while (treesToProcess.isEmpty())
						if (finishedBuildingTree)
							return;
						else
							treesToProcess.wait();
					operate(treesToProcess.removeFirst());
					run();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
		private synchronized void operate(Tree subTree) throws InterruptedException {
			switch (subTree.operation) {
			case "+":
				subTree.result = subTree.leftSon.result + subTree.rightSon.result;
				break;
			case "-":
				subTree.result = subTree.leftSon.result - subTree.rightSon.result;
				break;
			case "*":
				subTree.result = subTree.leftSon.result * subTree.rightSon.result;
				break;
			case "/":
				subTree.result = subTree.leftSon.result / subTree.rightSon.result;
				break;
			default:
				throw new IllegalArgumentException(subTree.operation + " is not a valid operation sign.");
			}
			if (subTree.father != null && subTree.father.isComputable())
				operate(subTree.father);
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
		private double result = 0.0d;

		/**
		 * To be computable it must have both the sons and the values of both
		 * must be numbers.
		 * 
		 * @return Is this is computable or not.
		 */
		public boolean isComputable() {
			return rightSon != null ? !Double.isNaN(leftSon.result) && !Double.isNaN(rightSon.result) : false;
		}

		Tree(Tree fatherSubTree, String operator) {
			result = Double.NaN;
			this.father = fatherSubTree;
			operation = operator;
			String ss = "";
			try {
				ss = stringTokenz.nextToken();
				leftSon = new Tree(Double.parseDouble(ss));
			}
			// If the element following the operation cannot be parsed into
			// a double, it must be an operation.
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
				rightSon = new Tree(Double.parseDouble(ss));
				// If the first element is a number and this also is, the
				// tree
				// is computable and is pushed into the list.
				if (!Double.isNaN(leftSon.result))
					synchronized (treesToProcess) {
						treesToProcess.addFirst(this);
						treesToProcess.notifyAll();
					}
			} catch (NumberFormatException e0) {
				rightSon = new Tree(this, ss);
			} catch (NoSuchElementException e1) {
				System.err.println("Malformed input i'm sorry.");
				System.exit(123);
			}
		}

		Tree(double value) {
			result = value;
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		ConcurrentCompute cc = new ConcurrentCompute("expression.txt");
	}
}
