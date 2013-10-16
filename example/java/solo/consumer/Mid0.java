
package solo.consumer;

import java.util.function.Consumer;

public class Mid0 extends PolyNode {

  public void accept(Node.NVisitor visitor) {
System.out.println("    Mid0.accept: TOP");
    Consumer<Mid0> func = visitor::visit;
    visitor.visit(this, func);
System.out.println("    Mid0.accept: BOTTOM");
  }

}

