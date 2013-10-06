
import java.util.function.Consumer;

public class Leaf00 implements Node {

  public void accept(Node.NVisitor visitor) {
System.out.println("        Leaf00.accept: TOP");
    Consumer<Leaf00> func = visitor::visit;
    visitor.visit(func, this);
System.out.println("        Leaf00.accept: BOTTOM");
  }
}

