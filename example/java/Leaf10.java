
import java.util.function.Consumer;

public class Leaf10 implements Node {

  public void accept(Node.NVisitor visitor) {
System.out.println("        Leaf10.accept: TOP");
    Consumer<Leaf10> func = visitor::visit;
    visitor.visit(func, this);
System.out.println("        Leaf10.accept: BOTTOM");
  }
}

