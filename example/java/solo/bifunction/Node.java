
package solo.bifunction;

import com.megaannum.visitor.support.Traversal;
import com.megaannum.visitor.solo.Visit.WithBiFunction;
import java.util.Collection;
import java.util.Collections;

public interface Node extends WithBiFunction.Target<Node,Node.NVisitor,Integer> {

  public interface NVisitor extends WithBiFunction.Agent<Node,NVisitor,Integer> {

      default public <D> Integer visit(Leaf00  leaf00, D data) {
        debug("NVisitor.visit Leaf00: " +data);
        return visit((Node) leaf00, data);
      }
      default public <D> Integer visit(Leaf01  leaf01, D data) {
        debug("NVisitor.visit Leaf01: " +data);
        return visit((Node) leaf01, data);
      }
      default public <D> Integer visit(Leaf10  leaf10, D data) {
        debug("NVisitor.visit Leaf10: " +data);
        return visit((Node) leaf10, data);
      }
      default public <D> Integer visit(Leaf11  leaf11, D data) {
        debug("NVisitor.visit Leaf11: " +data);
        return visit((Node) leaf11, data);
      }

      default public <D> Integer visit(Mid0  mid0, D data) {
        debug("NVisitor.visit Mid0: " +data);
        return visit((Node) mid0, data);
      }
      default public <D> Integer visit(Mid1  mid1, D data) {
        debug("  NVisitor.visit Mid1: " +data);
        return visit((Node) mid1, data);
      }

      default public <D> Integer visit(Top  top, D data) {
        debug("NVisitor.visit Top: " +data);
        return visit((Node) top, data);
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
      public <D> Integer mergeVisitCurrent(Integer currentResult,
                                       Integer newResult,
                                       Node node,
                                       D data) {
        if (currentResult == null) {
          return newResult;
        } else if (newResult == null) {
          return currentResult;
        } else {
          return currentResult+newResult;
        }
      }
      public <D> Integer mergeVisitTraversal(Integer currentResult,
                                            Integer newResult,
                                            Node node,
                                            D data) {
        if (currentResult == null) {
          return newResult;
        } else if (newResult == null) {
          return currentResult;
        } else {
          return currentResult+newResult;
        }
      }
      public <D> Integer mergeBreadth(Integer currentResult,
                                     Integer newResult,
                                     Node node,
                                     D data) {
        if (currentResult == null) {
          return newResult;
        } else if (newResult == null) {
          return currentResult;
        } else {
          return currentResult+newResult;
        }
      }
      public <D> Integer mergeTraversal(Integer currentResult,
                                       Integer newResult,
                                       Node node,
                                       D data) {
        if (currentResult == null) {
          return newResult;
        } else if (newResult == null) {
          return currentResult;
        } else {
          return currentResult+newResult;
        }
      }


      public <D> Integer visit(Leaf00  leaf00, D data) {
        System.out.println("          NV.visit Leaf00:" +data);
        // NVisitor.super.visit(leaf00);
        return 0;
      }
      public <D> Integer visit(Leaf01  leaf01, D data) {
        System.out.println("          NV.visit Leaf01:" +data);
        // NVisitor.super.visit(leaf01);
        return 1;
      }
      public <D> Integer visit(Leaf10  leaf10, D data) {
        System.out.println("          NV.visit Leaf10:" +data);
        // NVisitor.super.visit(leaf10);
        return 2;
      }
      public <D> Integer visit(Leaf11  leaf11, D data) {
        System.out.println("          NV.visit Leaf11:" +data);
        // NVisitor.super.visit(leaf11);
        return 3;
      }

      public <D> Integer visit(Mid0  mid0, D data) {
        Integer i = null;
        switch (traversal()) {
          case DEPTH_FIRST_PRE :
          case DEPTH_FIRST_POST :
            System.out.println("      NV.visit Mid0: " +traversal().name()+":"+data);
            i = 1;
            break;
          case DEPTH_FIRST_AROUND :
            System.out.println("      NV.visit Mid0: BEFORE " +traversal().name()+":"+data);
            i=traverse(mid0, data);
            System.out.println("      NV.visit Mid0: AFTER " +traversal().name()+":"+data);
            break;
          case BREADTH_FIRST :
            System.out.println("      NV.visit Mid0: " +traversal().name()+":"+data);
            i = 2;
            break;
        }
        // NVisitor.super.visit(mid0);
        return i;
      }
      public <D> Integer visit(Mid1  mid1, D data) {
        Integer i = null;
        switch (traversal()) {
          case DEPTH_FIRST_PRE :
          case DEPTH_FIRST_POST :
            System.out.println("      NV.visit Mid1: " +traversal().name()+":"+data);
            i = 2;
            break;
          case DEPTH_FIRST_AROUND :
            System.out.println("      NV.visit Mid1: BEFORE " +traversal().name()+":"+data);
            i=traverse(mid1, data);
            System.out.println("      NV.visit Mid1: AFTER " +traversal().name()+":"+data);
            break;
          case BREADTH_FIRST :
            System.out.println("      NV.visit Mid1: " +traversal().name()+":"+data);
            i = 2;
            break;
        }
        // NVisitor.super.visit(mid1);
        return i;
      }

      public <D> Integer visit(Top  top, D data) {
        Integer i = null;
        switch (traversal()) {
          case DEPTH_FIRST_PRE :
          case DEPTH_FIRST_POST :
            System.out.println("  NV.visit Top: " +traversal().name()+":"+data);
            i = 1;
            break;
          case DEPTH_FIRST_AROUND :
            System.out.println("  NV.visit Top: BEFORE " +traversal().name()+":"+data);
            i=traverse(top, data);
            System.out.println("  NV.visit Top: AFTER " +traversal().name()+":"+data);
            break;
          case BREADTH_FIRST :
            System.out.println("  NV.visit Top: " +traversal().name()+":"+data);
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
