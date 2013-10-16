
package solo.bifunction;

import java.util.function.BiFunction;

public class Leaf10 implements Node {

  public <D> Integer accept(Node.NVisitor visitor, D data) {
System.out.println("        Leaf10.accept: TOP");
    BiFunction<Leaf10,D,Integer> func = visitor::visit;
System.out.println("        Leaf10.accept: BOTTOM");
    return visitor.visit(this, data, func);
  }
}

