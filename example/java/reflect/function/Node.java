
package reflect.function;

import com.megaannum.visitor.support.Traversal;
import com.megaannum.visitor.reflect.function.Visitor;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Node {
  public static class S implements Supplier<Collection<Node>> {
    public static S sup = new S();
    public Collection<Node> get() {
      return Collections.emptyList();
    }
  }



  public interface NVisitor extends Visitor<Node,String> {
    default public String dovisit(Node node) {
      if (node instanceof Leaf00) {
        Function<Leaf00,String> func = this::visit;
        Supplier<Collection<Node>> sup = S.sup;
        return visit(node, func, sup);

      } else if (node instanceof Leaf01) {
        Function<Leaf01,String> func = this::visit;
        Supplier<Collection<Node>> sup = S.sup;
        return visit(node, func, sup);

      } else if (node instanceof Leaf10) {
        Function<Leaf10,String> func = this::visit;
        Supplier<Collection<Node>> sup = S.sup;
        return visit(node, func, sup);

      } else if (node instanceof Leaf11) {
        Function<Leaf11,String> func = this::visit;
        Supplier<Collection<Node>> sup = S.sup;
        return visit(node, func, sup);

      } else if (node instanceof Mid0) {
        Function<Mid0,String> func = this::visit;
        Supplier<Collection<Node>> sup = ((Mid0) node)::children;
        return visit(node, func, sup);

      } else if (node instanceof Mid1) {
        Function<Mid1,String> func = this::visit;
        Supplier<Collection<Node>> sup = ((Mid1) node)::children;
        return visit(node, func, sup);

      } else if (node instanceof Top) {
        Function<Top,String> func = this::visit;
        Supplier<Collection<Node>> sup = ((Top) node)::children;
        return visit(node, func, sup);

      } else {
        String  msg = "Unsupported Node: " +node.getClass().getName();
        throw new RuntimeException(msg);
      }
    }

    default public String visit(Node  node) {
      debug("NVisitor.visit Node: ");
      return "Node";
    }

    default public String visit(Leaf00  leaf00) {
      debug("NVisitor.visit Leaf00: ");
      return visit((Node) leaf00);
    }
    default public String visit(Leaf01  leaf01) {
      debug("NVisitor.visit Leaf01: ");
      return visit((Node) leaf01);
    }
    default public String visit(Leaf10  leaf10) {
      debug("NVisitor.visit Leaf10: ");
      return visit((Node) leaf10);
    }
    default public String visit(Leaf11  leaf11) {
      debug("NVisitor.visit Leaf11: ");
      return visit((Node) leaf11);
    }

    default public String visit(Mid0  mid0) {
      debug("NVisitor.visit Mid0: ");
      return visit((Node) mid0);
    }
    default public String visit(Mid1  mid1) {
      debug("  NVisitor.visit Mid1: ");
      return visit((Node) mid1);
    }

    default public String visit(Top  top) {
      debug("NVisitor.visit Top: ");
      return visit((Node) top);
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

    public String mergeVisitCurrent(String currentResult,
                                    String newResult,
                                    Node node) {
      if (currentResult == null) {
        return newResult;
      } else if (newResult == null) {
        return currentResult;
      } else {
        return currentResult+newResult;
      }
    }
    public String mergeVisitTraversal(String currentResult,
                                      String newResult,
                                      Node node) {
      if (currentResult == null) {
        return newResult;
      } else if (newResult == null) {
        return currentResult;
      } else {
        return currentResult+newResult;
      }
    }
    public String mergeBreadth(String currentResult,
                               String newResult,
                               Node node) {
      if (currentResult == null) {
        return newResult;
      } else if (newResult == null) {
        return currentResult;
      } else {
        return currentResult+newResult;
      }
    }
    public String mergeTraversal(String currentResult,
                                 String newResult,
                                 Node node) {
      if (currentResult == null) {
        return newResult;
      } else if (newResult == null) {
        return currentResult;
      } else {
        return currentResult+newResult;
      }
    }

    public String visit(Leaf00  leaf00) {
      System.out.println("          NV.visit Leaf00:");
      // NVisitor.super.visit(leaf00);
      return "Leaf00";
    }
    public String visit(Leaf01  leaf01) {
      System.out.println("          NV.visit Leaf01:");
      // NVisitor.super.visit(leaf01);
      return "Leaf01";
    }
    public String visit(Leaf10  leaf10) {
      System.out.println("          NV.visit Leaf10:");
      // NVisitor.super.visit(leaf10);
      return "Leaf10";
    }
    public String visit(Leaf11  leaf11) {
      System.out.println("          NV.visit Leaf11:");
      // NVisitor.super.visit(leaf11);
      return "Leaf11";
    }

    public String visit(Mid0  mid0) {
      String rval = "Mid0";
      switch (traversal()) {
        case DEPTH_FIRST_PRE :
        case DEPTH_FIRST_POST :
          System.out.println("      NV.visit Mid0: " +traversal().name());
          break;
        case DEPTH_FIRST_AROUND :
          System.out.println("      NV.visit Mid0: BEFORE " +traversal().name());
          Supplier<Collection<Node>> sup = mid0::children;
          String r = traverse(mid0, sup);
          rval=mergeTraversal(rval, r, mid0);
          System.out.println("      NV.visit Mid0: AFTER " +traversal().name());
          break;
        case BREADTH_FIRST :
          System.out.println("      NV.visit Mid0: " +traversal().name());
          break;
      }
      // NVisitor.super.visit(mid0);
      return rval;
    }
    public String visit(Mid1  mid1) {
      String rval = "Mid1";
      switch (traversal()) {
        case DEPTH_FIRST_PRE :
        case DEPTH_FIRST_POST :
          System.out.println("      NV.visit Mid1: " +traversal().name());
          break;
        case DEPTH_FIRST_AROUND :
          System.out.println("      NV.visit Mid1: BEFORE " +traversal().name());
          Supplier<Collection<Node>> sup = mid1::children;
          String r = traverse(mid1, sup);
          rval=mergeTraversal(rval, r, mid1);
          System.out.println("      NV.visit Mid1: AFTER " +traversal().name());
          break;
        case BREADTH_FIRST :
          System.out.println("      NV.visit Mid1: " +traversal().name());
          break;
      }
      // NVisitor.super.visit(mid1);
      return rval;
    }

    public String visit(Top  top) {
      String rval = "Top";
      switch (traversal()) {
        case DEPTH_FIRST_PRE :
        case DEPTH_FIRST_POST :
          System.out.println("  NV.visit Top: " +traversal().name());
          break;
        case DEPTH_FIRST_AROUND :
          System.out.println("  NV.visit Top: BEFORE " +traversal().name());
          Supplier<Collection<Node>> sup = top::children;
          String r = traverse(top, sup);
          rval=mergeTraversal(rval, r, top);
          System.out.println("  NV.visit Top: AFTER " +traversal().name());
          break;
        case BREADTH_FIRST :
          System.out.println("  NV.visit Top: " +traversal().name());
          break;
      }
      // NVisitor.super.visit(top);
      return rval;
    }

  }

  default public Collection<Node> children() {
    return Collections.emptyList();
  }

}
