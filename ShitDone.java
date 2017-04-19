import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

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

	List<Number> list = null;
	static Scanner SC = null;
static int	THREADCOUNT =0;

	ShitDone(String arg) {
		list = new ArrayList<Number>();
		try {
			SC = new Scanner(new BufferedInputStream(new FileInputStream(new File(arg))));
			SC.useDelimiter(" ");
			while (SC.hasNext()) {
				if (SC.hasNextDouble())
					list.add(SC.nextDouble());
				else {
					switch (SC.next()) {
					case ("+"):
						list.add((byte) 1);
						break;
					case ("-"):
						list.add((byte) 2);
						break;
					case ("*"):
						list.add((byte) 3);
						break;
					case ("/"):
						list.add((byte) 4);
						break;
					default:
						throw new Exception();
					}
				}
				// list.add((SC.hasNextDouble() ? SC.nextDouble() : SC.next()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.print("Finito di importare la robba fra.\r\n");
	}

	public double getShitDone() throws InterruptedException {
		boolean operatedThisRun;
		ArrayList<Thread> threadsRun = new ArrayList<Thread>();
		do {
			threadsRun.clear();
			operatedThisRun = false;
			for (int i = 0; i < list.size()-2; i++) {
				System.out.print(list.size() + "\r\n");
				if (list.get(i) instanceof Byte)
					if (list.get(i + 1) instanceof Double)
						if (list.get(i + 2) instanceof Double) {
							Thread toRun = new Thread(
									new Evaluator(i, (Double) list.remove(i + 1), (Double) list.remove(i + 1)));
							toRun.start();
							threadsRun.add(toRun);
							THREADCOUNT++;
							operatedThisRun = true;
						} else
							i++;
			}
			for (Thread in : threadsRun)
				in.join();
		} while (operatedThisRun);
		System.out.print("Il risultato e: "+list.get(0)+"\r\nUsando: "+THREADCOUNT+" thread.");
		return 0;
	}

	public static void main(String args[]) throws InterruptedException {

		// String path = "/home/bibar/Downloads/expression.txt";
		// String test = "- * / 15 - 7 + 1 1 3 + 2 + 1 1";
		ShitDone sh = new ShitDone("/home/bibar/Downloads/expression.txt");
		sh.getShitDone();
	}

	private class Evaluator implements Runnable {
		Number operation;
		Double operand0, operand1;
		int index;

		Evaluator(Number operator, double operand0, double operand1) {
			this.operation = list.get((int) operator);
			this.operand0 = operand0;
			this.operand1 = operand1;
			this.index = (int) operator;
		}

		@Override
		public void run() {
			// System.out.print("Sto facendo: " + operand0 + operation +
			// operand1 + "\r\n");
			switch ((byte) operation) {
			case (1):
				list.set(index, operand0 + operand1);
				break;
			case (2):
				list.set(index, operand0 - operand1);
				break;
			case (3):
				list.set(index, operand0 * operand1);
				break;
			case (4):
				list.set(index, operand0 / operand1);
				break;
			}
		}

	}
}
