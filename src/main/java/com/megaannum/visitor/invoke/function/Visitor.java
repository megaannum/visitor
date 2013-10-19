
package com.megaannum.visitor.invoke.function;

import com.megaannum.visitor.support.*;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.Collection;

public interface Visitor<NODE,R> {

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
   * Function function accept method can not have an Exception thrown.
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

  default public R mergeVisitCurrent(R currentResult,  
                                     R newResult, 
                                     NODE node) {
    return newResult;
  }
  default public R mergeVisitTraversal(R currentResult,  
                                       R newResult, 
                                       NODE node) {
    return newResult;
  }
  default public R mergeBreadth(R currentResult,  
                                R newResult, 
                                NODE node) {
    return newResult;
  }
  default public R mergeTraversal(R currentResult,  
                                  R newResult, 
                                  NODE node) {
    return newResult;
  }

  /** 
   * Default visit method for all nodes. Throws a
   * MissingImplementationException if the errorOnMissing returns true.
   * Does nothing otherwise.
   * 
   * @param node The NODE node.
   */
  default public R visit(NODE node) {
    debug("Visitor.visit: node=" + node.getClass().getName());

    if (errorOnMissing()) {
      String msg = "Missing 'visit' methods for type: " +
                     node.getClass().getName();
      throw new MissingImplementationException(msg);
    }

    return null;
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
                         Class<?> returnClz, 
                         Class<?> visitorClass) 
        throws Throwable {
    for (Class<?> clz : nodeClasses) {
      Pair<MethodHandle,MethodHandle> pair = generate(clz, returnClz, visitorClass);
      mhs.put(clz, pair);
    }
  }

  default public Pair<MethodHandle,MethodHandle> generate(
                                                  Class<?> clz,
                                                  Class<?> returnClz, 
                                                  Class<?> visitorClass) 
      throws Throwable {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType mt = MethodType.methodType(returnClz, clz);
    MethodHandle mh_visit = lookup.findVirtual(visitorClass, "visit", mt);
    mt = MethodType.methodType(Collection.class);
    MethodHandle mh_children = null;
    try {
      mh_children = lookup.findVirtual(clz, "children", mt);
    } catch (Throwable t) {
      // ignore
    }
    return new Pair<MethodHandle,MethodHandle>(mh_visit, mh_children);
  }

  Pair<MethodHandle,MethodHandle> lookup(Class<?> clz);

  default public R dovisit(NODE object) {
    Class<?> clz = object.getClass();
    Pair<MethodHandle,MethodHandle> pair = lookup(clz);
    return visit(object, pair);
  }

  @SuppressWarnings("unchecked")
  default public R visit(NODE node, Pair<MethodHandle,MethodHandle> pair) {
    debug("Visitor.visit: TOP " +node.getClass().getName());

    R retval = null;
    R new_retval = null;

    try {
      if (filter().visitNode(node)) {
        switch (traversal()) {
          case BREADTH_FIRST:
            retval=breadth(node, pair);
            break;
          case DEPTH_FIRST_PRE:
            new_retval=(R)pair.left.invoke(this, node);
            retval=mergeVisitCurrent(retval, new_retval, node);
            new_retval=traverse(node, pair.right);
            retval=mergeVisitTraversal(retval, new_retval, node);
            break;
          case DEPTH_FIRST_POST:
            new_retval=traverse(node, pair.right);
            retval=mergeVisitTraversal(retval, new_retval, node);
            new_retval=(R)pair.left.invoke(this, node);
            retval=mergeVisitCurrent(retval, new_retval, node);
            break;
          case DEPTH_FIRST_AROUND:
            new_retval=(R)pair.left.invoke(this, node);
            retval=mergeVisitCurrent(retval, new_retval, node);
            break;
        }
      }
    } catch (Throwable t) {
      setError(t);
    } finally {
      debug("Visitor.visit: BOTTOM " +node.getClass().getName());
      return retval;
    }
  }

  @SuppressWarnings("unchecked")
  default public R breadth(NODE node, Pair<MethodHandle,MethodHandle> pair) {
    BreadthControl<Pair<Pair<MethodHandle,MethodHandle>,NODE>> qc = getBreadthControl();
    qc.queue().enqueue(new Pair<Pair<MethodHandle,MethodHandle>,NODE>(pair, node));
    R retval = null;
    R new_retval = null;

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
            new_retval=(R)visit.invoke(this, nodeNext);
            retval=mergeBreadth(retval, new_retval, nodeNext);
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
              if (stop()) {
                break;
              }
              try {
                new_retval=dovisit(child);
                retval=mergeBreadth(retval, new_retval, child);
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
    return retval;
  }

  default public R dotraverse(NODE node) {
    Class<?> clz = node.getClass();
    Pair<MethodHandle,MethodHandle> pair = lookup(clz);
    return traverse(node, pair.right);
  }

  @SuppressWarnings("unchecked")
  default public R traverse(NODE node, MethodHandle children) {
    debug("Visitor.traverse: TOP " +node.getClass().getName());

    R retval = null;
    R new_retval = null;

    if (children != null && filter().visitChildren(node)) {
      final Collection<NODE> child_nodes;
      try {
        child_nodes = (Collection<NODE>)children.invoke(node);
      } catch (Throwable t) {
        setError(t);
        return retval;
      }
      for (NODE child : child_nodes) {
        if (stop()) {
          break;
        }
        debug("Visitor.traverse: call dovisit child=" +child.getClass().getName());
        try {
          new_retval=dovisit(child);
          retval=mergeTraversal(retval, new_retval, child);
        } catch (Throwable t) {
          setError(t);
        }
      }
    }

    debug("Visitor.traverse: BOTTOM " +node.getClass().getName());
    return retval;
  }

}

