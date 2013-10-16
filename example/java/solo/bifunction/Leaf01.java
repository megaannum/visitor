
package solo.bifunction;

import java.util.function.BiFunction;

public class Leaf01 implements Node {

  public <D> Integer accept(Node.NVisitor visitor, D data) {
System.out.println("        Leaf01.accept: TOP");
    BiFunction<Leaf01,D,Integer> func = visitor::visit;
System.out.println("        Leaf01.accept: BOTTOM");
    return visitor.visit(this, data, func);
  }
}

