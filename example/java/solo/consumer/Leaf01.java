
package solo.consumer;

import java.util.function.Consumer;

public class Leaf01 implements Node {

  public void accept(Node.NVisitor visitor) {
System.out.println("        Leaf01.accept: TOP");
    Consumer<Leaf01> func = visitor::visit;
    visitor.visit(this, func);
System.out.println("        Leaf01.accept: BOTTOM");
  }
}

