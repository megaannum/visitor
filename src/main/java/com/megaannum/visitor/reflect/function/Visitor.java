
package com.megaannum.visitor.reflect.function;

import com.megaannum.visitor.support.*;
import java.util.function.Function;
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
  default public BreadthControl<Triple<Function<?,R>,Supplier<?>,NODE>> getBreadthControl() {
    return (BreadthControl<Triple<Function<?,R>,Supplier<?>,NODE>>) BreadthControl.qcs.get();
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
   * Function function accept method can not have an Exception thrown.
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

  default R dovisit(NODE object) {
    return null;
  }

  @SuppressWarnings("unchecked")
  default public R visit(NODE node, Function<?,R> func, Supplier<?> sup) {
    debug("Visitor.visit: TOP " +node.getClass().getName());

    R retval = null;
    R new_retval = null;

    if (filter().visitNode(node)) {
      switch (traversal()) {
        case BREADTH_FIRST:
          retval=breadth(node, func, sup);
          break;
        case DEPTH_FIRST_PRE:
          new_retval=((Function<NODE,R>) func).apply(node);
          retval=mergeVisitCurrent(retval, new_retval, node);
          new_retval=traverse(node, sup);
          retval=mergeVisitTraversal(retval, new_retval, node);
          break;
        case DEPTH_FIRST_POST:
          new_retval=traverse(node, sup);
          retval=mergeVisitTraversal(retval, new_retval, node);
          new_retval=((Function<NODE,R>) func).apply(node);
          retval=mergeVisitCurrent(retval, new_retval, node);
          break;
        case DEPTH_FIRST_AROUND:
          new_retval=((Function<NODE,R>) func).apply(node);
          retval=mergeVisitCurrent(retval, new_retval, node);
          break;
      }
    }

    debug("Agent.visit: BOTTOM " +node.getClass().getName());

    return retval;
  }

  @SuppressWarnings("unchecked")
  default public R breadth(NODE node, Function<?,R> func, Supplier<?> sup) {
    BreadthControl<Triple<Function<?,R>,Supplier<?>,NODE>> qc = getBreadthControl();
    qc.queue().enqueue(new Triple<Function<?,R>,Supplier<?>,NODE>(func, sup, node));

    R retval = null;
    R new_retval = null;

    if (qc.notInBreadth()) {
      qc.enterBreadth();
      try {
        while (! qc.queue().isEmpty()) {
          if (stop()) {
            break;
          }
          Triple<Function<?,R>,Supplier<?>,NODE> triple = qc.queue().dequeue();
          Function<?,R> funcNext = triple.left;
          Supplier<?> supNext = triple.middle;
          NODE nodeNext = triple.right;
          new_retval=((Function<NODE,R>) funcNext).apply(nodeNext);
          retval=mergeBreadth(retval, new_retval, nodeNext);
          if (filter().visitChildren(nodeNext)) {
            for (NODE child : ((Supplier<Collection<NODE>>) supNext).get()) {
              new_retval=dovisit(child);
              retval=mergeBreadth(retval, new_retval, child);
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
  default public R traverse(NODE node, Supplier<?> sup) {
    debug("Visitor.traverse: TOP " +node.getClass().getName());

    R retval = null;
    R new_retval = null;

    if (filter().visitChildren(node)) {
      for (NODE child : ((Supplier<Collection<NODE>>) sup).get()) {
        if (stop()) {
          break;
        }
        debug("Visitor.traverse: call dovisit child=" +child.getClass().getName());
        new_retval=dovisit(child);
        retval=mergeTraversal(retval, new_retval, child);
      }
    }

    debug("Visitor.traverse: BOTTOM " +node.getClass().getName());
    return retval;
  }

}

