import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class meant to compute extremely large calculations over a mathematical
 * expression in PN. Operations are written like: <Operation>
 * <Operator0><Operator1> with a newline or a space as the divider between
 * members.
 * 
 * @author Vladyslav Sulimovskyy
 *
 */
public class ShitDone {
	// Service pool to control the number of thread spawned and the values
	// returned from the calculation.
	private static ExecutorService POOL = null;
	// Using java.util.Scanner because of it's ability to distinguish doubles
	// from operations.
	private static Scanner SC = null;

	ShitDone(String arg) {
		try {
			SC = new Scanner(new File(arg));
			SC.useDelimiter(" ");
		} catch (FileNotFoundException e) {
			System.err.println("Looks like " + arg + " is not a valid file.");
			// Using error code 404 for FileNotFound.
			System.exit(404);
		} catch (Exception e) {
			System.err.println("General exception during the scanner creation.");
			// Using 500 for general internal error.
			System.exit(500);
		}
		POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	public double getShitDone() {
		try {
			double result = POOL.submit(new Evaluator(SC.next())).get();
			System.out.println("The result of the computation was: " + result);
			POOL.shutdown();
			if (SC != null)
				SC.close();
			return result;
		} catch (InterruptedException e) {
			System.err.println("The main thread was interrupted in a bad way.");
			// 501 for bad interruption.
			System.exit(501);
		} catch (ExecutionException e) {
			System.err.println("An unexpected error occured during execution.");
			System.exit(500);
		}
		return -1.0;
	}

	public static void main(String args[]) {

		String path = "/home/bibar/Desktop/shieeet.txt";
		ShitDone shieeet = new ShitDone(path);
		shieeet.getShitDone();

	}

	private class Evaluator implements Callable<Double> {
		// Value used to evaluate the expression and return the result.
		double operand0, operand1;
		// Operation to perform on the operands.
		String operation;

		Evaluator(String operation) {
			// Only sum difference multiplication and division are supported.
			if (!operation.equals("/") && !operation.equals("*") && !operation.equals("+") && !operation.equals("-"))
				throw new IllegalArgumentException("Apparently the file is broken or does not conform to the PN");
			// At this point i know the operation is supported.
			this.operation = operation;
		}

		@Override
		public Double call() throws Exception {
			// If the value after the operation is not a double it must be an
			// operation and i must resolve it first.
			if (SC.hasNextDouble())
				operand0 = SC.nextDouble();
			else
				operand0 = POOL.submit(new Evaluator(SC.next())).get();
			if (SC.hasNextDouble())
				operand1 = SC.nextDouble();
			else
				operand1 = POOL.submit(new Evaluator(SC.next())).get();
			// Switch decision on the operation over the operands.
			switch (operation) {
			case ("+"):
				return operand0 + operand1;
			case ("-"):
				return operand0 - operand1;
			case ("*"):
				return operand0 * operand1;
			case ("/"):
				return operand0 / operand1;
			// Not really necessary.
			default:
				throw new IllegalArgumentException("It seems the file contains an unsupported operation.");
			}
		}
	}
}
