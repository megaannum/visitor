
package solo.bifunction;

import java.util.function.BiFunction;

public class Mid0 extends PolyNode {

  public <D> Integer accept(Node.NVisitor visitor, D data) {
System.out.println("    Mid0.accept: TOP");
    BiFunction<Mid0,D,Integer> func = visitor::visit;
System.out.println("    Mid0.accept: BOTTOM");
    return visitor.visit(this, data, func);
  }

}

