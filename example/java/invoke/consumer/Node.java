
package invoke.consumer;

import com.megaannum.visitor.support.Pair;
import com.megaannum.visitor.support.Traversal;
import com.megaannum.visitor.invoke.consumer.Visitor;
import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;

public interface Node {

  public abstract class NVisitor implements Visitor<Node> {
    public static final Class<?>[] nodeClasses = {
        Leaf00.class,
        Leaf01.class,
        Leaf10.class,
        Leaf11.class,
        Mid0.class,
        Mid1.class,
        Top.class
    };

    public static final Map<Class<?>,Pair<MethodHandle,MethodHandle>> mhs =
            new HashMap<Class<?>,Pair<MethodHandle,MethodHandle>>();

    
    protected NVisitor() throws Throwable {
      register(nodeClasses, mhs, NVisitor.class);
    }

    public Pair<MethodHandle,MethodHandle> lookup(Class<?> clz) {
      return mhs.get(clz);
    }


    public void visit(Node  node) {
      debug("NVisitor.visit Node: ");
    }

    public void visit(Leaf00  leaf00) {
      debug("NVisitor.visit Leaf00: ");
      visit((Node) leaf00);
    }
    public void visit(Leaf01  leaf01) {
      debug("NVisitor.visit Leaf01: ");
      visit((Node) leaf01);
    }
    public void visit(Leaf10  leaf10) {
      debug("NVisitor.visit Leaf10: ");
      visit((Node) leaf10);
    }
    public void visit(Leaf11  leaf11) {
      debug("NVisitor.visit Leaf11: ");
      visit((Node) leaf11);
    }

    public void visit(Mid0  mid0) {
      debug("NVisitor.visit Mid0: ");
      visit((Node) mid0);
    }
    public void visit(Mid1  mid1) {
      debug("  NVisitor.visit Mid1: ");
      visit((Node) mid1);
    }

    public void visit(Top  top) {
      debug("NVisitor.visit Top: ");
      visit((Node) top);
    }

  }
  public class NV extends NVisitor {
    private final Traversal tv;
    private Throwable throwable = null;

    public NV(Traversal tv) throws Throwable {
      super();
      this.tv = tv;
    }

    public Traversal traversal() {
      return tv;
    }

    public Throwable error() {
      return throwable;
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
          Pair<MethodHandle,MethodHandle> pair = lookup(Mid0.class);
          try {
            traverse(mid0, pair.right);
          } catch (Throwable t) {
            throwable = t;
          }
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
          Pair<MethodHandle,MethodHandle> pair = lookup(Mid1.class);
          try {
            traverse(mid1, pair.right);
          } catch (Throwable t) {
            throwable = t;
          }
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
          Pair<MethodHandle,MethodHandle> pair = lookup(Top.class);
          try {
            traverse(top, pair.right);
          } catch (Throwable t) {
            throwable = t;
          }
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
