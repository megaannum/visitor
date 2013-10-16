
package reflect.consumer;

import com.megaannum.visitor.support.Traversal;
import com.megaannum.visitor.reflect.consumer.Visitor;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Node {

  public static class S implements Supplier<Collection<Node>> {
    public static S sup = new S();
    public Collection<Node> get() {
      return Collections.emptyList();
    }
  }

  public interface NVisitor extends Visitor<Node> {
    default public void dovisit(Node node) {
      if (node instanceof Leaf00) {
        Consumer<Leaf00> func = this::visit;
        Supplier<Collection<Node>> sup = S.sup;
        visit(node, func, sup);

      } else if (node instanceof Leaf01) {
        Consumer<Leaf01> func = this::visit;
        Supplier<Collection<Node>> sup = S.sup;
        visit(node, func, sup);

      } else if (node instanceof Leaf10) {
        Consumer<Leaf10> func = this::visit;
        Supplier<Collection<Node>> sup = S.sup;
        visit(node, func, sup);

      } else if (node instanceof Leaf11) {
        Consumer<Leaf11> func = this::visit;
        Supplier<Collection<Node>> sup = S.sup;
        visit(node, func, sup);

      } else if (node instanceof Mid0) {
        Consumer<Mid0> func = this::visit;
        Supplier<Collection<Node>> sup = ((Mid0) node)::children;
        visit(node, func, sup);

      } else if (node instanceof Mid1) {
        Consumer<Mid1> func = this::visit;
        Supplier<Collection<Node>> sup = ((Mid1) node)::children;
        visit(node, func, sup);

      } else if (node instanceof Top) {
        Consumer<Top> func = this::visit;
        Supplier<Collection<Node>> sup = ((Top) node)::children;
        visit(node, func, sup);

      } else {
        String  msg = "Unsupported Node: " +node.getClass().getName();
        throw new RuntimeException(msg);
      }
    }

    default public void visit(Node  node) {
      debug("NVisitor.visit Node: ");
    }

    default public void visit(Leaf00  leaf00) {
      debug("NVisitor.visit Leaf00: ");
      visit((Node) leaf00);
    }
    default public void visit(Leaf01  leaf01) {
      debug("NVisitor.visit Leaf01: ");
      visit((Node) leaf01);
    }
    default public void visit(Leaf10  leaf10) {
      debug("NVisitor.visit Leaf10: ");
      visit((Node) leaf10);
    }
    default public void visit(Leaf11  leaf11) {
      debug("NVisitor.visit Leaf11: ");
      visit((Node) leaf11);
    }

    default public void visit(Mid0  mid0) {
      debug("NVisitor.visit Mid0: ");
      visit((Node) mid0);
    }
    default public void visit(Mid1  mid1) {
      debug("  NVisitor.visit Mid1: ");
      visit((Node) mid1);
    }

    default public void visit(Top  top) {
      debug("NVisitor.visit Top: ");
      visit((Node) top);
    }

  }
  public class NV implements NVisitor {
    private final Traversal tv;

    public NV(Traversal tv) {
      this.tv = tv;
    }

    public Traversal traversal() {
      return tv;
    }

    public void visit(Leaf00  leaf00) {
      System.out.println("          NV.visit Leaf00:");
      // NVisitor.super.visit(leaf00);
    }
    public void visit(Leaf01  leaf01) {
      System.out.println("          NV.visit Leaf01:");
      // NVisitor.super.visit(leaf01);
    }
    public void visit(Leaf10  leaf10) {
      System.out.println("          NV.visit Leaf10:");
      // NVisitor.super.visit(leaf10);
    }
    public void visit(Leaf11  leaf11) {
      System.out.println("          NV.visit Leaf11:");
      // NVisitor.super.visit(leaf11);
    }

    public void visit(Mid0  mid0) {
      switch (traversal()) {
        case DEPTH_FIRST_PRE :
        case DEPTH_FIRST_POST :
          System.out.println("      NV.visit Mid0: " +traversal().name());
          break;
        case DEPTH_FIRST_AROUND :
          System.out.println("      NV.visit Mid0: BEFORE " +traversal().name());
          Supplier<Collection<Node>> sup = mid0::children;
          traverse(mid0, sup);
          System.out.println("      NV.visit Mid0: AFTER " +traversal().name());
          break;
        case BREADTH_FIRST :
          System.out.println("      NV.visit Mid0: " +traversal().name());
          break;
      }
      // NVisitor.super.visit(mid0);
    }
    public void visit(Mid1  mid1) {
      switch (traversal()) {
        case DEPTH_FIRST_PRE :
        case DEPTH_FIRST_POST :
          System.out.println("      NV.visit Mid1: " +traversal().name());
          break;
        case DEPTH_FIRST_AROUND :
          System.out.println("      NV.visit Mid1: BEFORE " +traversal().name());
          Supplier<Collection<Node>> sup = mid1::children;
          traverse(mid1, sup);
          System.out.println("      NV.visit Mid1: AFTER " +traversal().name());
          break;
        case BREADTH_FIRST :
          System.out.println("      NV.visit Mid1: " +traversal().name());
          break;
      }
      // NVisitor.super.visit(mid1);
    }

    public void visit(Top  top) {
      switch (traversal()) {
        case DEPTH_FIRST_PRE :
        case DEPTH_FIRST_POST :
          System.out.println("  NV.visit Top: " +traversal().name());
          break;
        case DEPTH_FIRST_AROUND :
          System.out.println("  NV.visit Top: BEFORE " +traversal().name());
          Supplier<Collection<Node>> sup = top::children;
          traverse(top, sup);
          System.out.println("  NV.visit Top: AFTER " +traversal().name());
          break;
        case BREADTH_FIRST :
          System.out.println("  NV.visit Top: " +traversal().name());
          break;
      }
      // NVisitor.super.visit(top);
    }

  }

  default public Collection<Node> children() {
    return Collections.emptyList();
  }

}
