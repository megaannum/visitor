
package solo.bifunction;

import java.util.function.BiFunction;

public class Mid1 extends PolyNode {

  public <D> Integer accept(Node.NVisitor visitor, D data) {
System.out.println("    Mid1.accept: TOP");
    BiFunction<Mid1,D,Integer> func = visitor::visit;
System.out.println("    Mid1.accept: BOTTOM");
    return visitor.visit(this, data, func);
  }

}

