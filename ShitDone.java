import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Shiet {
	private class Calcola extends Thread {
		String operator;
		int index;
		double result;

		public double get() {
			return result;
		}

		Calcola(String index) {
			this.operator = index;
		}

		@Override
		public void run() {
			THREADCOUNT++;
			double operator0, operator1;
			if (sc.hasNextDouble())
				operator0 = sc.nextDouble();
			else {
				Calcola c = new Calcola(sc.next());
				c.start();
				try {
					c.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				operator0 = c.get();
			}
			if (sc.hasNextDouble())
				operator1 = sc.nextDouble();
			else {
				Calcola c = new Calcola(sc.next());
				c.start();
				try {
					c.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				operator1 = c.get();
			}
			switch (operator) {
			case ("+"):
				result = operator0 + operator1;
				break;
			case ("-"):
				result = operator0 - operator1;
				break;
			case ("*"):
				result = operator0 * operator1;
				break;
			case ("/"):
				result = operator0 / operator1;
				break;
			}
			// TODO Auto-generated method stub

		}
	}

	private static  int THREADCOUNT = 0;

	
	static Scanner sc = null;

	Shiet(String arg) {
		try {
			long time = System.nanoTime();
			
			sc = new Scanner(new BufferedInputStream(new FileInputStream(new File(arg))));
			sc.useDelimiter(" ");
			Calcola c = new Calcola(sc.next());
			c.start();
			c.join();
			System.out.print("Il risultato e': " + c.get() + "\r\n");
			System.out.print("Computato in: "+(System.nanoTime()-time)/1000000000+" secondi\r\n");
			System.out.print("Usando: "+THREADCOUNT+" thread\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Shiet sh = new Shiet("/home/bibar/Downloads/expression.txt");
	}

}
