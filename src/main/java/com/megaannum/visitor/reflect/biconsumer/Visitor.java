
package com.megaannum.visitor.reflect.biconsumer;

import com.megaannum.visitor.support.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
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
  default public <D> BreadthControl<Triple<BiConsumer<?,D>,Supplier<?>,NODE>> getBreadthControl() {
    return (BreadthControl<Triple<BiConsumer<?,D>,Supplier<?>,NODE>>) BreadthControl.qcs.get();
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
   * BiConsumer function accept method can not have an Exception thrown.
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
  default public <D> void visit(NODE node, D data) {
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
   * @return a BiFilter
   */
  default public BiFilter filter() {
    return new BiFilter(){ };
  }

  <D> void dovisit(NODE object, D data);

  @SuppressWarnings("unchecked")
  default public <D> void visit(NODE node, D data, BiConsumer<?,D> func, Supplier<?> sup) {
    debug("Visitor.visit: TOP " +node.getClass().getName());

    if (filter().visitNode(node, data)) {
      switch (traversal()) {
        case BREADTH_FIRST:
          breadth(node, data, func, sup);
          break;
        case DEPTH_FIRST_PRE:
          ((BiConsumer<NODE,D>) func).accept(node, data);
          traverse(node, data, sup);
          break;
        case DEPTH_FIRST_POST:
          traverse(node, data, sup);
          ((BiConsumer<NODE,D>) func).accept(node, data);
          break;
        case DEPTH_FIRST_AROUND:
          ((BiConsumer<NODE,D>) func).accept(node, data);
          break;
      }
    }

    debug("Agent.visit: BOTTOM " +node.getClass().getName());
  }

  @SuppressWarnings("unchecked")
  default public <D> void breadth(NODE node, D data, BiConsumer<?,D> func, Supplier<?> sup) {
    BreadthControl<Triple<BiConsumer<?,D>,Supplier<?>,NODE>> qc = getBreadthControl();
    qc.queue().enqueue(new Triple<BiConsumer<?,D>,Supplier<?>,NODE>(func, sup, node));

    if (qc.notInBreadth()) {
      qc.enterBreadth();
      try {
        while (! qc.queue().isEmpty()) {
          if (stop()) {
            break;
          }
          Triple<BiConsumer<?,D>,Supplier<?>,NODE> triple = qc.queue().dequeue();
          BiConsumer<?,D> funcNext = triple.left;
          Supplier<?> supNext = triple.middle;
          NODE nodeNext = triple.right;
          ((BiConsumer<NODE,D>) funcNext).accept(nodeNext, data);
          if (filter().visitChildren(nodeNext, data)) {
            for (NODE child : ((Supplier<Collection<NODE>>) supNext).get()) {
              dovisit(child, data);
            }
          }
        }
      } finally {
        qc.leaveBreadth();
      }
    }
  }

  @SuppressWarnings("unchecked")
  default public <D> void traverse(NODE node, D data, Supplier<?> sup) {
    debug("Visitor.traverse: TOP " +node.getClass().getName());

    if (filter().visitChildren(node, data)) {
      for (NODE child : ((Supplier<Collection<NODE>>) sup).get()) {
        if (stop()) {
          break;
        }
        debug("Visitor.traverse: call dovisit child=" +child.getClass().getName());
        dovisit(child, data);
      }
    }

    debug("Visitor.traverse: BOTTOM " +node.getClass().getName());
  }

}

