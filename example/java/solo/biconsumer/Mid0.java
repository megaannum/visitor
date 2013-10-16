
package solo.biconsumer;

import java.util.function.BiConsumer;

public class Mid0 extends PolyNode {

  public <D> void accept(Node.NVisitor visitor, D data) {
System.out.println("    Mid0.accept: TOP");
    BiConsumer<Mid0,D> func = visitor::visit;
    visitor.visit(this, data, func);
System.out.println("    Mid0.accept: BOTTOM");
  }

}

