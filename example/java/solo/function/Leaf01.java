
package solo.function;

import java.util.function.Function;

public class Leaf01 implements Node {

  public Integer accept(Node.NVisitor visitor) {
System.out.println("        Leaf01.accept: TOP");
    Function<Leaf01, Integer> func = visitor::visit;
System.out.println("        Leaf01.accept: BOTTOM");
    return visitor.visit(this, func);
  }
}

