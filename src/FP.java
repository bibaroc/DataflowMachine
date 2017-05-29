import java.util.*;
import java.io.*;
 
public class FP {
  private Random r;
  private FileWriter out;
  private final int DEPTH = 20;
  public static void main(String [] args) { new FP(); }
  FP() {
     r = new Random();
     String e;
     try{    
        out=new FileWriter("expression.txt");
        e = go(DEPTH);
        out.write(e);
        out.close();
    } catch (Exception f) { f.printStackTrace(System.out); }
  }
  private String go(int n) {
     if (n > 0) {
       switch(r.nextInt(3)) {
           case 0: return "+ "+go(n-1)+" "+go(n-1); 
           case 1: return "- "+go(n-1)+" "+go(n-1); 
           case 2: return "* "+go(n-1)+" "+go(n-1); 
           case 3: return "/ "+go(n-1)+" "+go(n-1);
       }
     } else return ""+(r.nextDouble());
     return "";
  }
}