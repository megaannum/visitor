
package solo.function;

import java.util.function.Function;

public class Mid0 extends PolyNode {

  public Integer accept(Node.NVisitor visitor) {
System.out.println("    Mid0.accept: TOP");
    Function<Mid0,Integer> func = visitor::visit;
System.out.println("    Mid0.accept: BOTTOM");
    return visitor.visit(this, func);
  }

}

