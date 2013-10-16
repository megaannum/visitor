
package solo.bifunction;

import java.util.function.BiFunction;

public class Leaf11 implements Node {

  public <D> Integer accept(Node.NVisitor visitor, D data) {
System.out.println("        Leaf11.accept: TOP");
    BiFunction<Leaf11,D,Integer> func = visitor::visit;
System.out.println("        Leaf11.accept: BOTTOM");
    return visitor.visit(this, data, func);
  }
}

