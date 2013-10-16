
package com.megaannum.visitor.reflect.bifunction;

import com.megaannum.visitor.support.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
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
  default public <D> BreadthControl<Triple<BiFunction<?,D,R>,Supplier<?>,NODE>> getBreadthControl() {
    return (BreadthControl<Triple<BiFunction<?,D,R>,Supplier<?>,NODE>>) BreadthControl.qcs.get();
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
   * Has the Visitor had a Throwable. This approach is used because the
   * BiFunction function accept method can not have an Exception thrown.
   * 
   * @return 
   */
  default public boolean hadError() {
    return error() != null;
  }
  
  /** 
   * The Throwable that a visit method generated. Implementations of the
   * Visit.Agent need to override this method so that if a Throwable is
   * generated, the Agent can safe it and return it in the method that
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

  default public <D> R mergeVisitCurrent(R currentResult,  
                                         R newResult, 
                                         NODE node,
                                         D data) {
    return newResult;
  }
  default public <D> R mergeVisitTraversal(R currentResult,  
                                           R newResult, 
                                           NODE node,
                                           D data) {
    return newResult;
  }
  default public <D> R mergeBreadth(R currentResult,  
                                    R newResult, 
                                    NODE node,
                                    D data) {
    return newResult;
  }
  default public <D> R mergeTraversal(R currentResult,  
                                      R newResult, 
                                      NODE node,
                                      D data) {
    return newResult;
  }

  /** 
   * Default visit method for all nodes. Throws a
   * MissingImplementationException if the errorOnMissing returns true.
   * Does nothing otherwise.
   * 
   * @param node The NODE node.
   */
  default public <D> R visit(NODE node, D data) {
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
   * @return a BiFilter
   */
  default public BiFilter filter() {
    return new BiFilter(){ };
  }

  default <D> R dovisit(NODE object, D data) {
    return null;
  }

  @SuppressWarnings("unchecked")
  default public <D> R visit(NODE node, D data, BiFunction<?,D,R> func, Supplier<?> sup) {
    debug("Visitor.visit: TOP " +node.getClass().getName());

    R retval = null;
    R new_retval = null;

    if (filter().visitNode(node, data)) {
      switch (traversal()) {
        case BREADTH_FIRST:
          retval=breadth(node, data, func, sup);
          break;
        case DEPTH_FIRST_PRE:
          new_retval=((BiFunction<NODE,D,R>) func).apply(node, data);
          retval=mergeVisitCurrent(retval, new_retval, node, data);
          new_retval=traverse(node, data, sup);
          retval=mergeVisitTraversal(retval, new_retval, node, data);
          break;
        case DEPTH_FIRST_POST:
          new_retval=traverse(node, data, sup);
          retval=mergeVisitTraversal(retval, new_retval, node, data);
          new_retval=((BiFunction<NODE,D,R>) func).apply(node, data);
          retval=mergeVisitCurrent(retval, new_retval, node, data);
          break;
        case DEPTH_FIRST_AROUND:
          new_retval=((BiFunction<NODE,D,R>) func).apply(node, data);
          retval=mergeVisitCurrent(retval, new_retval, node, data);
          break;
      }
    }

    debug("Agent.visit: BOTTOM " +node.getClass().getName());

    return retval;
  }

  @SuppressWarnings("unchecked")
  default public <D> R breadth(NODE node, D data, BiFunction<?,D,R> func, Supplier<?> sup) {
    BreadthControl<Triple<BiFunction<?,D,R>,Supplier<?>,NODE>> qc = getBreadthControl();
    qc.queue().enqueue(new Triple<BiFunction<?,D,R>,Supplier<?>,NODE>(func, sup, node));

    R retval = null;
    R new_retval = null;

    if (qc.notInBreadth()) {
      qc.enterBreadth();
      try {
        while (! qc.queue().isEmpty()) {
          if (stop()) {
            break;
          }
          Triple<BiFunction<?,D,R>,Supplier<?>,NODE> triple = qc.queue().dequeue();
          BiFunction<?,D,R> funcNext = triple.left;
          Supplier<?> supNext = triple.middle;
          NODE nodeNext = triple.right;
          ((BiFunction<NODE,D,R>) funcNext).apply(nodeNext, data);
          if (filter().visitChildren(nodeNext, data)) {
            for (NODE child : ((Supplier<Collection<NODE>>) supNext).get()) {
              new_retval=dovisit(child, data);
              retval=mergeBreadth(retval, new_retval, child, data);
            }
          }
        }
      } finally {
        qc.leaveBreadth();
      }
    }

    return retval;
  }

  @SuppressWarnings("unchecked")
  default public <D> R traverse(NODE node, D data, Supplier<?> sup) {
    debug("Visitor.traverse: TOP " +node.getClass().getName());

    R retval = null;
    R new_retval = null;

    if (filter().visitChildren(node, data)) {
      for (NODE child : ((Supplier<Collection<NODE>>) sup).get()) {
        if (stop()) {
          break;
        }
        debug("Visitor.traverse: call dovisit child=" +child.getClass().getName());
        new_retval=dovisit(child, data);
        retval=mergeTraversal(retval, new_retval, child, data);
      }
    }

    debug("Visitor.traverse: BOTTOM " +node.getClass().getName());
    return retval;
  }

}

