
package solo.biconsumer;

import java.util.function.BiConsumer;

public class Leaf01 implements Node {

  public <D> void accept(Node.NVisitor visitor, D data) {
System.out.println("        Leaf01.accept: TOP");
    BiConsumer<Leaf01,D> func = visitor::visit;
    visitor.visit(this, data, func);
System.out.println("        Leaf01.accept: BOTTOM");
  }
}

