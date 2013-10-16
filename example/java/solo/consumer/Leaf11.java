
package solo.consumer;

import java.util.function.Consumer;

public class Leaf11 implements Node {

  public void accept(Node.NVisitor visitor) {
System.out.println("        Leaf11.accept: TOP");
    Consumer<Leaf11> func = visitor::visit;
    visitor.visit(this, func);
System.out.println("        Leaf11.accept: BOTTOM");
  }
}

