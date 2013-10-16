
package solo.function;

import java.util.function.Function;

public class Leaf10 implements Node {

  public Integer accept(Node.NVisitor visitor) {
System.out.println("        Leaf10.accept: TOP");
    Function<Leaf10,Integer> func = visitor::visit;
System.out.println("        Leaf10.accept: BOTTOM");
    return visitor.visit(this, func);
  }
}

