import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Scanner;

public class PrefixEvaluator {

	public static void main(String[] args) throws FileNotFoundException {
		//Non so perche' questo vuole i punti sui double.
		long StartTime = System.currentTimeMillis();
		ArrayList<String> arr = new ArrayList<String>();
		Scanner sc = new Scanner(new File("C:/Users/Bibaroc/Desktop/Workspace/DataFlowMachine/expression.txt"));
		while(sc.hasNext())
			arr.add(sc.next());
		Deque<Double> stack = new ArrayDeque<>();
		for (int i = arr.size() - 1; i > -1; i--) {
			String s = arr.get(i);
			if (s.equals("")) {
				continue;
			}
			if (s.equals("+"))
				stack.push(stack.poll() + stack.poll());
			else if (s.equals("*"))
				stack.push(stack.poll() * stack.poll());
			else if (s.equals("/"))
				stack.push(stack.poll() / stack.poll());
			else if (s.equals("-"))
				stack.push(stack.poll() - stack.poll());

			else
				stack.push(Double.parseDouble(s));

		}
		System.out.println("Result: "+stack.poll());
		System.out.println("After: "+(System.currentTimeMillis()-StartTime));
	}

}
