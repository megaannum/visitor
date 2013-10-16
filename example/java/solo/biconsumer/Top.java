
package solo.biconsumer;

import java.util.function.BiConsumer;

public class Top extends PolyNode {

  public <D> void accept(Node.NVisitor visitor, D data) {
System.out.println("Top.accept: TOP");
    BiConsumer<Top,D> func = visitor::visit;
    visitor.visit(this, data, func);
System.out.println("Top.accept: BOTTOM");
  }

}

