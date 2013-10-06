
import java.util.function.Consumer;

public class Top extends PolyNode {

  public void accept(Node.NVisitor visitor) {
System.out.println("Top.accept: TOP");
    Consumer<Top> func = visitor::visit;
    visitor.visit(func, this);
System.out.println("Top.accept: BOTTOM");
  }

}

