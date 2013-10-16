
package solo.bifunction;

import java.util.function.BiFunction;

public class Top extends PolyNode {

  public <D> Integer accept(Node.NVisitor visitor, D data) {
System.out.println("Top.accept: TOP");
    BiFunction<Top,D,Integer> func = visitor::visit;
System.out.println("Top.accept: BOTTOM");
    return visitor.visit(this, data, func);
  }

}

