
package solo.biconsumer;

import java.util.function.BiConsumer;

public class Leaf11 implements Node {

  public <D> void accept(Node.NVisitor visitor, D data) {
System.out.println("        Leaf11.accept: TOP");
    BiConsumer<Leaf11,D> func = visitor::visit;
    visitor.visit(this, data, func);
System.out.println("        Leaf11.accept: BOTTOM");
  }
}

