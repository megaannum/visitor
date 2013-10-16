
package solo.function;

import java.util.function.Function;

public class Leaf11 implements Node {

  public Integer accept(Node.NVisitor visitor) {
System.out.println("        Leaf11.accept: TOP");
    Function<Leaf11,Integer> func = visitor::visit;
System.out.println("        Leaf11.accept: BOTTOM");
    return visitor.visit(this, func);
  }
}

