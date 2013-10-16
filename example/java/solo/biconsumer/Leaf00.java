
package solo.biconsumer;

import java.util.function.BiConsumer;

public class Leaf00 implements Node {

  public <D> void accept(Node.NVisitor visitor, D data) {
System.out.println("        Leaf00.accept: TOP");
    BiConsumer<Leaf00,D> func = visitor::visit;
    visitor.visit(this, data, func);
System.out.println("        Leaf00.accept: BOTTOM");
  }
}

