
package solo.function;

import java.util.function.Function;

public class Mid1 extends PolyNode {

  public Integer accept(Node.NVisitor visitor) {
System.out.println("    Mid1.accept: TOP");
    Function<Mid1,Integer> func = visitor::visit;
System.out.println("    Mid1.accept: BOTTOM");
    return visitor.visit(this, func);
  }

}

