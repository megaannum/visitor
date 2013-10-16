
package solo.bifunction;

import java.util.function.BiFunction;

public class Leaf00 implements Node {

  public <D> Integer accept(Node.NVisitor visitor, D data) {
System.out.println("        Leaf00.accept: TOP");
    BiFunction<Leaf00,D,Integer> func = visitor::visit;
System.out.println("        Leaf00.accept: BOTTOM");
    return visitor.visit(this, data, func);
  }
}

