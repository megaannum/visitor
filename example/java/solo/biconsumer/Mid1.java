
package solo.biconsumer;

import java.util.function.BiConsumer;

public class Mid1 extends PolyNode {

  public <D> void accept(Node.NVisitor visitor, D data) {
System.out.println("    Mid1.accept: TOP");
    BiConsumer<Mid1,D> func = visitor::visit;
    visitor.visit(this, data, func);
System.out.println("    Mid1.accept: BOTTOM");
  }

}

