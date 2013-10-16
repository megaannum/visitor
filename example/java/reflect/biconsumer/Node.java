
package reflect.biconsumer;

import com.megaannum.visitor.support.Traversal;
import com.megaannum.visitor.support.Pair;
import com.megaannum.visitor.reflect.biconsumer.Visitor;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface Node {

  public static class S implements Supplier<Collection<Node>> {
    public static S sup = new S();
    public Collection<Node> get() {
      return Collections.emptyList();
    }
  }

  public abstract class NVisitor implements Visitor<Node> {

    public static final int Leaf00_id = 0;
    public static final int Leaf01_id = 1;
    public static final int Leaf10_id = 2;
    public static final int Leaf11_id = 3;
    public static final int Mid0_id = 4;
    public static final int Mid1_id = 5;
    public static final int Top_id = 6;

    protected static List<Pair<BiConsumer<Object,Object>,Supplier<Collection<Node>>>> nodeToPair = null;


    @SuppressWarnings({"unchecked"})
    public <D> void dovisit(Node node, D data) {
      switch (node.id()) {
        case Leaf00_id :
          BiConsumer<Leaf00,D> func00 = this::visit;
          Supplier<Collection<Node>> sup00 = S.sup;
          visit(node, data, func00, sup00);
          break;
        case Leaf01_id :
          BiConsumer<Leaf01,D> func01 = this::visit;
          Supplier<Collection<Node>> sup01 = S.sup;
          visit(node, data, func01, sup01);
          break;
        case Leaf10_id :
          BiConsumer<Leaf10,D> func10 = this::visit;
          Supplier<Collection<Node>> sup10 = S.sup;
          visit(node, data, func10, sup10);
          break;
        case Leaf11_id :
          BiConsumer<Leaf11,D> func11 = this::visit;
          Supplier<Collection<Node>> sup11 = S.sup;
          visit(node, data, func11, sup11);
          break;
        case Mid0_id :
          BiConsumer<Mid0,D> func_m0 = this::visit;
          Supplier<Collection<Node>> sup_m0 = ((Mid0) node)::children;
          visit(node, data, func_m0, sup_m0);
          break;
        case Mid1_id :
          BiConsumer<Mid1,D> func_m1 = this::visit;
          Supplier<Collection<Node>> sup_m1 = ((Mid1) node)::children;
          visit(node, data, func_m1, sup_m1);
          break;
        case Top_id :
          BiConsumer<Top,D> func = this::visit;
          Supplier<Collection<Node>> sup = ((Top) node)::children;
          visit(node, data, func, sup);
          break;
        default :
          String  msg = "Unsupported Node: " +node.getClass().getName();
          throw new RuntimeException(msg);
      }
    }

    public <D> void visit(Node  node, D data) {
      debug("NVisitor.visit Node: " +data);
    }

    public <D> void visit(Leaf00  leaf00, D data) {
      debug("NVisitor.visit Leaf00: " +data);
      visit((Node) leaf00, data);
    }
    public <D> void visit(Leaf01  leaf01, D data) {
      debug("NVisitor.visit Leaf01: " +data);
      visit((Node) leaf01, data);
    }
    public <D> void visit(Leaf10  leaf10, D data) {
      debug("NVisitor.visit Leaf10: " +data);
      visit((Node) leaf10, data);
    }
    public <D> void visit(Leaf11  leaf11, D data) {
      debug("NVisitor.visit Leaf11: " +data);
      visit((Node) leaf11, data);
    }

    public <D> void visit(Mid0  mid0, D data) {
      debug("NVisitor.visit Mid0: " +data);
      visit((Node) mid0, data);
    }
    public <D> void visit(Mid1  mid1, D data) {
      debug("  NVisitor.visit Mid1: " +data);
      visit((Node) mid1, data);
    }

    public <D> void visit(Top  top, D data) {
      debug("NVisitor.visit Top: " +data);
      visit((Node) top, data);
    }

  }
  public class NV extends NVisitor {
    private final Traversal tv;

    public NV(Traversal tv) {
      this.tv = tv;
    }

    public Traversal traversal() {
      return tv;
    }

    public <D> void visit(Leaf00  leaf00, D data) {
      System.out.println("          NV.visit Leaf00:" +data);
      // NVisitor.super.visit(leaf00, data);
    }
    public <D> void visit(Leaf01  leaf01, D data) {
      System.out.println("          NV.visit Leaf01:" +data);
      // NVisitor.super.visit(leaf01, data);
    }
    public <D> void visit(Leaf10  leaf10, D data) {
      System.out.println("          NV.visit Leaf10:" +data);
      // NVisitor.super.visit(leaf10, data);
    }
    public <D> void visit(Leaf11  leaf11, D data) {
      System.out.println("          NV.visit Leaf11:" +data);
      // NVisitor.super.visit(leaf11, data);
    }

    public <D> void visit(Mid0  mid0, D data) {
      switch (traversal()) {
        case DEPTH_FIRST_PRE :
        case DEPTH_FIRST_POST :
          System.out.println("      NV.visit Mid0: " +traversal().name() +" "+data);
          break;
        case DEPTH_FIRST_AROUND :
          System.out.println("      NV.visit Mid0: BEFORE " +traversal().name() +" "+data);
          Supplier<Collection<Node>> sup = mid0::children;
          traverse(mid0, data, sup);
          System.out.println("      NV.visit Mid0: AFTER " +traversal().name() +" "+data);
          break;
        case BREADTH_FIRST :
          System.out.println("      NV.visit Mid0: " +traversal().name() +" "+data);
          break;
      }
      // NVisitor.super.visit(mid0, data);
    }
    public <D> void visit(Mid1  mid1, D data) {
      switch (traversal()) {
        case DEPTH_FIRST_PRE :
        case DEPTH_FIRST_POST :
          System.out.println("      NV.visit Mid1: " +traversal().name() +" "+data);
          break;
        case DEPTH_FIRST_AROUND :
          System.out.println("      NV.visit Mid1: BEFORE " +traversal().name() +" "+data);
          Supplier<Collection<Node>> sup = mid1::children;
          traverse(mid1, data, sup);
          System.out.println("      NV.visit Mid1: AFTER " +traversal().name() +" "+data);
          break;
        case BREADTH_FIRST :
          System.out.println("      NV.visit Mid1: " +traversal().name() +" "+data);
          break;
      }
      // NVisitor.super.visit(mid1, data);
    }

    public <D> void visit(Top  top, D data) {
      switch (traversal()) {
        case DEPTH_FIRST_PRE :
        case DEPTH_FIRST_POST :
          System.out.println("  NV.visit Top: " +traversal().name() +" "+data);
          break;
        case DEPTH_FIRST_AROUND :
          System.out.println("  NV.visit Top: BEFORE " +traversal().name() +" "+data);
          Supplier<Collection<Node>> sup = top::children;
          traverse(top, data, sup);
          System.out.println("  NV.visit Top: AFTER " +traversal().name() +" "+data);
          break;
        case BREADTH_FIRST :
          System.out.println("  NV.visit Top: " +traversal().name() +" "+data);
          break;
      }
      // NVisitor.super.visit(top, data);
    }

  }

  default public Collection<Node> children() {
    return Collections.emptyList();
  }

  int id();
}
