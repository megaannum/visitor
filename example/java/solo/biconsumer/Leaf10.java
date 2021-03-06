
package solo.biconsumer;

import java.util.function.BiConsumer;

public class Leaf10 implements Node {

  public <D> void accept(Node.NVisitor visitor, D data) {
System.out.println("        Leaf10.accept: TOP");
    BiConsumer<Leaf10,D> func = visitor::visit;
    visitor.visit(this, data, func);
System.out.println("        Leaf10.accept: BOTTOM");
  }
}

