
import com.megaannum.visitor.Visit;
import com.megaannum.visitor.Visit.Agent;

public class Main {
  public static void usage(String msg) {
    if (msg != null) {
      System.out.println(msg);
    }
    System.out.println("Main options");
    System.out.println("  Options:");
    System.out.println("    -h or --help");
    System.out.println("       print this message");
    System.out.println("    -t tval or --traversal tval : default(DEPTH_FIRST_PRE)");
    System.out.println("       Set traversal (DEPTH_FIRST_PRE, DEPTH_FIRST_POST, DEPTH_FIRST_AROUND, BREADTH_FIRST)");
    System.exit(0);
  }
  protected static void example(Node.NVisitor v) throws Exception {
    Leaf00 leaf00 = new Leaf00();
    Leaf01 leaf01 = new Leaf01();

    Mid0 mid0 = new Mid0();
    mid0.children().add(leaf00);
    mid0.children().add(leaf01);

    Leaf10 leaf10 = new Leaf10();
    Leaf11 leaf11 = new Leaf11();

    Mid1 mid1 = new Mid1();
    mid1.children().add(leaf10);
    mid1.children().add(leaf11);

    Top top = new Top();

    top.children().add(mid0);
    top.children().add(mid1);

    top.accept(v);
  }
  protected static Agent.Traversal getTraversal(String tstr) throws Exception {
    switch (tstr) {
      case  "DEPTH_FIRST_PRE" :
        return Agent.Traversal.DEPTH_FIRST_PRE;
      case  "DEPTH_FIRST_POST" :
        return Agent.Traversal.DEPTH_FIRST_POST;
      case  "DEPTH_FIRST_AROUND" :
        return Agent.Traversal.DEPTH_FIRST_AROUND;
      case  "BREADTH_FIRST" :
        return Agent.Traversal.BREADTH_FIRST;
    }
    String msg = "Bad traversal name: " +tstr;
    throw new Exception(msg);
  }
  public static void main(String[] args) throws Exception {
    String tname = "DEPTH_FIRST_PRE";

    int i = 0;
    while (i < args.length) {
      String arg = args[i];

      if (arg.startsWith("-")) {
        if (arg.equals("-h") || arg.equals("--help")) {
          usage(null);
        } else if (arg.equals("-t") || arg.equals("--traversal")) {
          i++;
          if (i == args.length) {
            usage("Missing traversal value");
          }
          tname = args[i];
        }
      } else {
        usage("Unrecognized argument: " +arg);
      }

      i++;
    }
    Agent.Traversal traversal = getTraversal(tname);

    Node.NVisitor v = new Node.NV(traversal);
    example(v);
  }
}
