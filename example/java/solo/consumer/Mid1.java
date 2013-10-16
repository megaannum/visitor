
package solo.consumer;

import java.util.function.Consumer;

public class Mid1 extends PolyNode {

  public void accept(Node.NVisitor visitor) {
System.out.println("    Mid1.accept: TOP");
    Consumer<Mid1> func = visitor::visit;
    visitor.visit(this, func);
System.out.println("    Mid1.accept: BOTTOM");
  }

}

