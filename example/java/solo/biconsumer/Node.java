
package solo.biconsumer;

import com.megaannum.visitor.support.Traversal;
import com.megaannum.visitor.solo.Visit.WithBiConsumer;
import java.util.Collection;
import java.util.Collections;

public interface Node extends WithBiConsumer.Target<Node, Node.NVisitor> {

  public interface NVisitor extends WithBiConsumer.Agent<Node,NVisitor> {
      default public <D> void visit(Leaf00  leaf00, D data) {
        debug("NVisitor.visit Leaf00: " +data);
        visit((Node) leaf00, data);
      }
      default public <D> void visit(Leaf01  leaf01, D data) {
        debug("NVisitor.visit Leaf01: " +data);
        visit((Node) leaf01, data);
      }
      default public <D> void visit(Leaf10  leaf10, D data) {
        debug("NVisitor.visit Leaf10: " +data);
        visit((Node) leaf10, data);
      }
      default public <D> void visit(Leaf11  leaf11, D data) {
        debug("NVisitor.visit Leaf11: " +data);
        visit((Node) leaf11, data);
      }

      default public <D> void visit(Mid0  mid0, D data) {
        debug("NVisitor.visit Mid0: " +data);
        visit((Node) mid0, data);
      }
      default public <D> void visit(Mid1  mid1, D data) {
        debug("  NVisitor.visit Mid1: " +data);
        visit((Node) mid1, data);
      }

      default public <D> void visit(Top  top, D data) {
        debug("NVisitor.visit Top: " +data);
        visit((Node) top, data);
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

      public <D> void visit(Leaf00  leaf00, D data) {
        System.out.println("          NV.visit Leaf00:" +data);
        // NVisitor.super.visit(leaf00);
      }
      public <D> void visit(Leaf01  leaf01, D data) {
        System.out.println("          NV.visit Leaf01:" +data);
        // NVisitor.super.visit(leaf01);
      }
      public <D> void visit(Leaf10  leaf10, D data) {
        System.out.println("          NV.visit Leaf10:" +data);
        // NVisitor.super.visit(leaf10);
      }
      public <D> void visit(Leaf11  leaf11, D data) {
        System.out.println("          NV.visit Leaf11:" +data);
        // NVisitor.super.visit(leaf11);
      }

      public <D> void visit(Mid0  mid0, D data) {
        switch (traversal()) {
          case DEPTH_FIRST_PRE :
          case DEPTH_FIRST_POST :
            System.out.println("      NV.visit Mid0: " +traversal().name()+":"+data);
            break;
          case DEPTH_FIRST_AROUND :
            System.out.println("      NV.visit Mid0: BEFORE " +traversal().name()+":"+data);
            traverse(mid0, data);
            System.out.println("      NV.visit Mid0: AFTER " +traversal().name()+":"+data);
            break;
          case BREADTH_FIRST :
            System.out.println("      NV.visit Mid0: " +traversal().name()+":"+data);
            break;
        }
        // NVisitor.super.visit(mid0);
      }
      public <D> void visit(Mid1  mid1, D data) {
        switch (traversal()) {
          case DEPTH_FIRST_PRE :
          case DEPTH_FIRST_POST :
            System.out.println("      NV.visit Mid1: " +traversal().name()+":"+data);
            break;
          case DEPTH_FIRST_AROUND :
            System.out.println("      NV.visit Mid1: BEFORE " +traversal().name()+":"+data);
            traverse(mid1, data);
            System.out.println("      NV.visit Mid1: AFTER " +traversal().name()+":"+data);
            break;
          case BREADTH_FIRST :
            System.out.println("      NV.visit Mid1: " +traversal().name()+":"+data);
            break;
        }
        // NVisitor.super.visit(mid1);
      }

      public <D> void visit(Top  top, D data) {
        switch (traversal()) {
          case DEPTH_FIRST_PRE :
          case DEPTH_FIRST_POST :
            System.out.println("  NV.visit Top: " +traversal().name()+":"+data);
            break;
          case DEPTH_FIRST_AROUND :
            System.out.println("  NV.visit Top: BEFORE " +traversal().name()+":"+data);
            traverse(top, data);
            System.out.println("  NV.visit Top: AFTER " +traversal().name()+":"+data);
            break;
          case BREADTH_FIRST :
            System.out.println("  NV.visit Top: " +traversal().name()+":"+data);
            break;
        }
        // NVisitor.super.visit(top);
      }

  }

  default public Collection<Node> children() {
    return Collections.emptyList();
  }

}
