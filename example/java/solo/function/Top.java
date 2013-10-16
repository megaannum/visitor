
package solo.function;

import java.util.function.Function;

public class Top extends PolyNode {

  public Integer accept(Node.NVisitor visitor) {
System.out.println("Top.accept: TOP");
    Function<Top,Integer> func = visitor::visit;
System.out.println("Top.accept: BOTTOM");
    return visitor.visit(this, func);
  }

}

