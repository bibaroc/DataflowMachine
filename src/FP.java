import java.util.*;
import java.io.*;

/**
 * The code I personaly use to generate a random polish notation expression. It
 * works by recursively calling the main function DEPTH times and each time
 * deciding uppon the operations to perform.
 * 
 */
public class FP {
	private Random r;
	private FileWriter out;
	private final int DEPTH = 20;

	public static void main(String[] args) {
		new FP();
	}

	FP() {
		r = new Random();
		String e;
		Scanner st;
		try {
			out = new FileWriter("expression.txt");
			e = go(DEPTH);
			st = new Scanner(e);
			while (st.hasNext())
				out.write(" " + st.next());
			out.close();
		} catch (Exception f) {
			System.exit(1);
		}
	}

	private String go(int n) {
		if (n > 0)
			switch (r.nextInt(3)) {
			case 0:
				return "+ " + go(n - 1) + " " + go(n - 1);
			case 1:
				return "- " + go(n - 1) + " " + go(n - 1);
			case 2:
				return "* " + go(n - 1) + " " + go(n - 1);
			case 3:
				return "/ " + go(n - 1) + " " + go(n - 1);
			}
		else
			return "" + r.nextDouble() * 100.0;
		return null;

	}
}