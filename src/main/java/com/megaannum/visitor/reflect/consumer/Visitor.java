
package com.megaannum.visitor.reflect.consumer;

import com.megaannum.visitor.support.*;
import java.util.function.Consumer;
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
  default public BreadthControl<Triple<Consumer<?>,Supplier<?>,NODE>> getBreadthControl() {
    return (BreadthControl<Triple<Consumer<?>,Supplier<?>,NODE>>) BreadthControl.qcs.get();
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
   * Consumer function accept method can not have an Exception thrown.
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

  void dovisit(NODE object);

  @SuppressWarnings("unchecked")
  default public void visit(NODE node, Consumer<?> func, Supplier<?> sup) {
    debug("Visitor.visit: TOP " +node.getClass().getName());

    if (filter().visitNode(node)) {
      switch (traversal()) {
        case BREADTH_FIRST:
          breadth(node, func, sup);
          break;
        case DEPTH_FIRST_PRE:
          ((Consumer<NODE>) func).accept(node);
          traverse(node, sup);
          break;
        case DEPTH_FIRST_POST:
          traverse(node, sup);
          ((Consumer<NODE>) func).accept(node);
          break;
        case DEPTH_FIRST_AROUND:
          ((Consumer<NODE>) func).accept(node);
          break;
      }
    }

    debug("Agent.visit: BOTTOM " +node.getClass().getName());
  }

  @SuppressWarnings("unchecked")
  default public void breadth(NODE node, Consumer<?> func, Supplier<?> sup) {
    BreadthControl<Triple<Consumer<?>,Supplier<?>,NODE>> qc = getBreadthControl();
    qc.queue().enqueue(new Triple<Consumer<?>,Supplier<?>,NODE>(func, sup, node));

    if (qc.notInBreadth()) {
      qc.enterBreadth();
      try {
        while (! qc.queue().isEmpty()) {
          if (stop()) {
            break;
          }
          Triple<Consumer<?>,Supplier<?>,NODE> triple = qc.queue().dequeue();
          Consumer<?> funcNext = triple.left;
          Supplier<?> supNext = triple.middle;
          NODE nodeNext = triple.right;
          ((Consumer<NODE>) funcNext).accept(nodeNext);
          if (filter().visitChildren(nodeNext)) {
            for (NODE child : ((Supplier<Collection<NODE>>) supNext).get()) {
              dovisit(child);
            }
          }
        }
      } finally {
        qc.leaveBreadth();
      }
    }
  }

  @SuppressWarnings("unchecked")
  default public void traverse(NODE node, Supplier<?> sup) {
    debug("Visitor.traverse: TOP " +node.getClass().getName());

    if (filter().visitChildren(node)) {
      for (NODE child : ((Supplier<Collection<NODE>>) sup).get()) {
        if (stop()) {
          break;
        }
        debug("Visitor.traverse: call dovisit child=" +child.getClass().getName());
        dovisit(child);
      }
    }

    debug("Visitor.traverse: BOTTOM " +node.getClass().getName());
  }

}

