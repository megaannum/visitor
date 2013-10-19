
package com.megaannum.visitor.invoke.consumer;

import com.megaannum.visitor.support.*;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.Collection;

public interface Visitor<NODE> {

  /** 
   * Gets the current Traversal value; 
   * 
   * @return traversal value.
   */
  Traversal traversal();

  /** 
   * This returns the default BreadthControl mechanism which used a ThreadLocal.
   * The ThreadLocal approach is not optimum and disallows the Visitor from
   * being passed from one Thread to another.
   *
   * The ThreadLocal approach is a way to get an Interface with default
   * methods some (fake, hacked) way of having data. For real applications
   * you might consider re-defining this method if you want to do any
   * breadth-first traversals.
   * 
   * @return 
   */
  @SuppressWarnings("unchecked")
  default public BreadthControl<Pair<Pair<MethodHandle,MethodHandle>,NODE>> getBreadthControl() {
    return (BreadthControl<Pair<Pair<MethodHandle,MethodHandle>,NODE>>) BreadthControl.qcs.get();
  }

  /** 
   * Set the Visitor into debug mode (if true) and no debug mode otherwise. 
   * 
   * @param debug 
   */
  default public void setDebug(boolean debug) {
  }

  /** 
   * Visitor's built-in debug methods.
   * 
   * @param msg debug message to be printed
   */
  default public void debug(String msg) {
    // emtpy
  }

  /** 
   * Set the Visitor into debug mode (if true) and no debug mode otherwise. 
   * 
   * @param debug 
   */
  default public void setError(Throwable throwable) {
    // empty
  }

  /** 
   * Has the Visitor had a Throwable. This approach is used because the
   * other visitors use it. TODO review.
   * 
   * @return 
   */
  default public boolean hadError() {
    return error() != null;
  }
  
  /** 
   * The Throwable that a visit method generated. Implementations of the
   * Visitor need to override this method so that if a Throwable is
   * generated, the Visitor can safe it and return it in the method that
   * overrides this method.
   * 
   * @return Throwable generated in visit method.
   */
  default public Throwable error() {
    return null;
  }

  /** 
   * Stop the Visitor traversal if this returns true.
   * 
   * @return true if traversal should stop and false otherwise.
   */
  default public boolean stop() {
    return hadError() || false;
  }

  /** 
   * Sets error on missing value, if true, a missing visit method triggers
   * a MissingImplementationException.
   * 
   * @param error_on_missing 
   */
  default public void setErrorOnMissing(boolean error_on_missing) {
    // empty
  }

  /** 
   * Should throw a MissingImplementationException if a node's visit
   * method is missing (so that the base, default, visit method is called)
   * if true is returned.
   * 
   * @return true if Exception should be thrown if node's visit method
   * is not defined and false no Exception is thrown.
   */
  default public boolean errorOnMissing() {
    return false;
  }

  /** 
   * The RuntimeException that is thrown if a node's visit method is
   * not defined (so that the default visit method is called) and the
   * method errorOnMissing returns true.
   */
  public class MissingImplementationException extends RuntimeException {
    public MissingImplementationException(String msg) {
      super(msg);
    }
  }

  /** 
   * Default visit method for all nodes. Throws a
   * MissingImplementationException if the errorOnMissing returns true.
   * Does nothing otherwise.
   * 
   * @param node The NODE node.
   */
  default public void visit(NODE node) {
    debug("Visitor.visit: node=" + node.getClass().getName());

    if (errorOnMissing()) {
      String msg = "Missing 'visit' methods for type: " +
                     node.getClass().getName();
      throw new MissingImplementationException(msg);
    }
  }

  /** 
   * The Filter used to determine if a node or a node's children should
   * be visited.
   * 
   * @return a Filter
   */
  default public Filter filter() {
    return new Filter(){ };
  }


  default public void register(
                         Class<?>[] nodeClasses, 
                         Map<Class<?>,Pair<MethodHandle,MethodHandle>> mhs,
                         Class<?> visitorClass) 
        throws Throwable {
    for (Class<?> nodeClass : nodeClasses) {
      Pair<MethodHandle,MethodHandle> pair = generate(nodeClass, visitorClass);
      mhs.put(nodeClass, pair);
    }
  }

  default public Class<?> getNodeCollectionClass() {
    return Collection.class;
  }
  default public String getNodeCollectionMethodName() {
    return "children";
  }
  default public Pair<MethodHandle,MethodHandle> generate(
                                                  Class<?> nodeClass,
                                                  Class<?> visitorClass) 
      throws Throwable {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType mt = MethodType.methodType(void.class, nodeClass);
    MethodHandle mh_visit = lookup.findVirtual(visitorClass, "visit", mt);

    Class<?> collectionClass = getNodeCollectionClass();
    String collectionMethodName = getNodeCollectionMethodName();
    mt = MethodType.methodType(collectionClass);
    MethodHandle mh_children = lookup.findVirtual(nodeClass, 
                                                  collectionMethodName, 
                                                  mt);
    return new Pair<MethodHandle,MethodHandle>(mh_visit, mh_children);
  }

  Pair<MethodHandle,MethodHandle> lookup(Class<?> nodeClass);




  default public void dovisit(NODE node) {
    debug("Visitor.dovisit: TOP " +node.getClass().getName());

    Class<?> clz = node.getClass();
    Pair<MethodHandle,MethodHandle> pair = lookup(clz);
    visit(node, pair);

    debug("Visitor.dovisit: BOTTOM " +node.getClass().getName());
  }

  @SuppressWarnings("unchecked")
  default public void visit(NODE node, Pair<MethodHandle,MethodHandle> pair) {
    debug("Visitor.visit: TOP " +node.getClass().getName());

    try {
      if (filter().visitNode(node)) {
        switch (traversal()) {
          case BREADTH_FIRST:
            breadth(node, pair);
            break;
          case DEPTH_FIRST_PRE:
            pair.left.invoke(this, node);
            traverse(node, pair.right);
            break;
          case DEPTH_FIRST_POST:
            traverse(node, pair.right);
            pair.left.invoke(this, node);
            break;
          case DEPTH_FIRST_AROUND:
            pair.left.invoke(this, node);
            break;
        }
      }
    } catch (Throwable t) {
      setError(t);
    } finally {
      debug("Visitor.visit: BOTTOM " +node.getClass().getName());
    }
  }

  @SuppressWarnings("unchecked")
  default public void breadth(NODE node, Pair<MethodHandle,MethodHandle> pair) {
    BreadthControl<Pair<Pair<MethodHandle,MethodHandle>,NODE>> qc = getBreadthControl();
    qc.queue().enqueue(new Pair<Pair<MethodHandle,MethodHandle>,NODE>(pair, node));

    if (qc.notInBreadth()) {
      qc.enterBreadth();
      try {
        while (! qc.queue().isEmpty()) {
          if (stop()) {
            break;
          }
          Pair<Pair<MethodHandle,MethodHandle>,NODE> pairNext = qc.queue().dequeue();
          MethodHandle visit = pairNext.left.left;
          MethodHandle children = pairNext.left.right;
          NODE nodeNext = pairNext.right;
          try {
            visit.invoke(this, nodeNext);
          } catch (Throwable t) {
            setError(t);
            break;
          }
          if (children != null && filter().visitChildren(nodeNext)) {
            final Collection<NODE> child_nodes;
            try {
              child_nodes = (Collection<NODE>)children.invoke(nodeNext);
            } catch (Throwable t) {
              setError(t);
              break;
            }
            for (NODE child : child_nodes) {
              try {
                dovisit(child);
              } catch (Throwable t) {
                setError(t);
              }
            }
          }
        }
      } finally {
        qc.leaveBreadth();
      }
    }
  }

  default public void dotraverse(NODE node) {
    debug("Visitor.dotraverse: TOP " +node.getClass().getName());

    Class<?> clz = node.getClass();
    Pair<MethodHandle,MethodHandle> pair = lookup(clz);
    traverse(node, pair.right);

    debug("Visitor.dotraverse: BOTTOM " +node.getClass().getName());
  }

  @SuppressWarnings("unchecked")
  default public void traverse(NODE node, MethodHandle children) {
    debug("Visitor.traverse: TOP " +node.getClass().getName());

    if (children != null && filter().visitChildren(node)) {
      final Collection<NODE> child_nodes;
      try {
        child_nodes = (Collection<NODE>)children.invoke(node);
      } catch (Throwable t) {
        setError(t);
        return;
      }
      for (NODE child : child_nodes) {
        if (stop()) {
          break;
        }
        debug("Visitor.traverse: call dovisit child=" +child.getClass().getName());
        try {
          dovisit(child);
        } catch (Throwable t) {
          setError(t);
        }
      }
    }

    debug("Visitor.traverse: BOTTOM " +node.getClass().getName());
  }

}

