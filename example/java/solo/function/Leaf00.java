
package solo.function;

import java.util.function.Function;

public class Leaf00 implements Node {

  public Integer accept(Node.NVisitor visitor) {
System.out.println("        Leaf00.accept: TOP");
    Function<Leaf00, Integer> func = visitor::visit;
System.out.println("        Leaf00.accept: BOTTOM");
    return visitor.visit(this, func);
  }
}

