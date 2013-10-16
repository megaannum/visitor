
// package biconsumer;

import com.megaannum.visitor.support.Pair;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;

public class Test {
  public static class Leaf {
    public String toString() {
      return "LEAF";
    }
  }
  public static class Top {
    protected List<Object> list = new ArrayList<Object>();
    public void add(Object obj) {
      list.add(obj);
    }
    public Collection<Object> children() {
      return list;
    }
    public String toString() {
      return "TOP";
    }
  }


  public static class Visitor {

    public static final Class<?>[] nodeClasses = {
        Leaf.class,
        Top.class
    };

    public static final Map<Class<?>,Pair<MethodHandle,MethodHandle>> mhs =
            new HashMap<Class<?>,Pair<MethodHandle,MethodHandle>>();

    public Visitor() throws Throwable {
      register();
    }
    protected void register() throws Throwable {
      for (Class<?> clz : nodeClasses) {
        Pair<MethodHandle,MethodHandle> pair = generate(clz);
        mhs.put(clz, pair);
      }
    }

    protected Pair<MethodHandle,MethodHandle> generate(Class<?> clz) throws Throwable {
      MethodHandles.Lookup lookup = MethodHandles.lookup();
      MethodType mt = MethodType.methodType(void.class, clz);
      MethodHandle mh_visit = lookup.findVirtual(Visitor.class, "visit", mt);
      mt = MethodType.methodType(Collection.class);
      MethodHandle mh_children = null;
      try {
        mh_children = lookup.findVirtual(clz, "children", mt);
      } catch (Throwable t) {
        // ignore
      }
      return new Pair<MethodHandle,MethodHandle>(mh_visit, mh_children);
    }

    public void visit(Top top) {
      System.out.println("Visitor.visit: Top " +top);
    }
    public void visit(Leaf leaf) {
      System.out.println("Visitor.visit: Leaf " +leaf);
    }
    public void dovisit(Object object) throws Throwable {
      Class<?> clz = object.getClass();
      Pair<MethodHandle,MethodHandle> pair = mhs.get(clz);
      visit(object, pair);
    }
    public void visit(Object object, 
                      Pair<MethodHandle,MethodHandle> pair) throws Throwable {
System.out.println("Visitor.visit: object: " +object);
      pair.left.invoke(this, object);
      traverse(object, pair);
    }

    public void traverse(Object object, 
                      Pair<MethodHandle,MethodHandle> pair) throws Throwable {
      if (pair.right != null) {
        for (Object o : (Collection<Object>)pair.right.invoke(object)) {
          dovisit(o);
        }
      }
    }
  }

  public static void main(String[] args) throws Throwable {
    Top top = new Top();
    Leaf leaf = new Leaf();
    top.add(leaf);
    Visitor visitor = new Visitor();
    visitor.dovisit(top);
  }
}

