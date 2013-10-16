
package solo.function;

import com.megaannum.visitor.support.Traversal;
import com.megaannum.visitor.solo.Visit.WithFunction;
import java.util.Collection;
import java.util.Collections;

public interface Node extends WithFunction.Target<Node,Node.NVisitor,Integer> {

  public interface NVisitor extends WithFunction.Agent<Node,NVisitor,Integer> {

      default public Integer visit(Leaf00  leaf00) {
        debug("NVisitor.visit Leaf00: ");
        return visit((Node) leaf00);
      }
      default public Integer visit(Leaf01  leaf01) {
        debug("NVisitor.visit Leaf01: ");
        return visit((Node) leaf01);
      }
      default public Integer visit(Leaf10  leaf10) {
        debug("NVisitor.visit Leaf10: ");
        return visit((Node) leaf10);
      }
      default public Integer visit(Leaf11  leaf11) {
        debug("NVisitor.visit Leaf11: ");
        return visit((Node) leaf11);
      }

      default public Integer visit(Mid0  mid0) {
        debug("NVisitor.visit Mid0: ");
        return visit((Node) mid0);
      }
      default public Integer visit(Mid1  mid1) {
        debug("  NVisitor.visit Mid1: ");
        return visit((Node) mid1);
      }

      default public Integer visit(Top  top) {
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
      public Integer mergeVisitCurrent(Integer currentResult,
                                       Integer newResult,
                                       Node node) {
        if (currentResult == null) {
          return newResult;
        } else if (newResult == null) {
          return currentResult;
        } else {
          return currentResult+newResult;
        }
      }
      public Integer mergeVisitTraversal(Integer currentResult,
                                         Integer newResult,
                                         Node node) {
        if (currentResult == null) {
          return newResult;
        } else if (newResult == null) {
          return currentResult;
        } else {
          return currentResult+newResult;
        }
      }
      public Integer mergeBreadth(Integer currentResult,
                                  Integer newResult,
                                  Node node) {
        if (currentResult == null) {
          return newResult;
        } else if (newResult == null) {
          return currentResult;
        } else {
          return currentResult+newResult;
        }
      }
      public Integer mergeTraversal(Integer currentResult,
                                    Integer newResult,
                                    Node node) {
        if (currentResult == null) {
          return newResult;
        } else if (newResult == null) {
          return currentResult;
        } else {
          return currentResult+newResult;
        }
      }


      public Integer visit(Leaf00  leaf00) {
        System.out.println("          NV.visit Leaf00:");
        // NVisitor.super.visit(leaf00);
        return 0;
      }
      public Integer visit(Leaf01  leaf01) {
        System.out.println("          NV.visit Leaf01:");
        // NVisitor.super.visit(leaf01);
        return 1;
      }
      public Integer visit(Leaf10  leaf10) {
        System.out.println("          NV.visit Leaf10:");
        // NVisitor.super.visit(leaf10);
        return 2;
      }
      public Integer visit(Leaf11  leaf11) {
        System.out.println("          NV.visit Leaf11:");
        // NVisitor.super.visit(leaf11);
        return 3;
      }

      public Integer visit(Mid0  mid0) {
        Integer i = null;
        switch (traversal()) {
          case DEPTH_FIRST_PRE :
          case DEPTH_FIRST_POST :
            System.out.println("      NV.visit Mid0: " +traversal().name());
            i = 1;
            break;
          case DEPTH_FIRST_AROUND :
            System.out.println("      NV.visit Mid0: BEFORE " +traversal().name());
            i=traverse(mid0);
            System.out.println("      NV.visit Mid0: AFTER " +traversal().name());
            break;
          case BREADTH_FIRST :
            System.out.println("      NV.visit Mid0: " +traversal().name());
            i = 2;
            break;
        }
        // NVisitor.super.visit(mid0);
        return i;
      }
      public Integer visit(Mid1  mid1) {
        Integer i = null;
        switch (traversal()) {
          case DEPTH_FIRST_PRE :
          case DEPTH_FIRST_POST :
            System.out.println("      NV.visit Mid1: " +traversal().name());
            i = 2;
            break;
          case DEPTH_FIRST_AROUND :
            System.out.println("      NV.visit Mid1: BEFORE " +traversal().name());
            i=traverse(mid1);
            System.out.println("      NV.visit Mid1: AFTER " +traversal().name());
            break;
          case BREADTH_FIRST :
            System.out.println("      NV.visit Mid1: " +traversal().name());
            i = 2;
            break;
        }
        // NVisitor.super.visit(mid1);
        return i;
      }

      public Integer visit(Top  top) {
        Integer i = null;
        switch (traversal()) {
          case DEPTH_FIRST_PRE :
          case DEPTH_FIRST_POST :
            System.out.println("  NV.visit Top: " +traversal().name());
            i = 1;
            break;
          case DEPTH_FIRST_AROUND :
            System.out.println("  NV.visit Top: BEFORE " +traversal().name());
            i=traverse(top);
            System.out.println("  NV.visit Top: AFTER " +traversal().name());
            break;
          case BREADTH_FIRST :
            System.out.println("  NV.visit Top: " +traversal().name());
            i = 2;
            break;
        }
        // NVisitor.super.visit(top);
        return i;
      }

  }

  default public Collection<Node> children() {
    return Collections.emptyList();
  }

}
