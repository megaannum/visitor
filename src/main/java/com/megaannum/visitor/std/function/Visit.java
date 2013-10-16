
package com.megaannum.visitor.std.function;

import com.megaannum.visitor.support.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;

/** 
 * Visitor Pattern for Java8 with node and child-node filtering, 
 * traveral mechanism support, stop, halt on error and error on
 * missing implementation support.
 * 
 * @author Richard M. Emberson
 * @since Oct 04 2013
 */
public interface Visit {

  /**
   * Target Visitable Objects 
   */
  public interface Target<VISITABLE extends Target<VISITABLE,VISITOR,R>, VISITOR extends Agent<VISITABLE,VISITOR,R>,R> {

    /**
     * Accept method used to implement the visitor pattern.  
     *
     * @param visitor Visitor instance.
     */
    R accept(VISITOR visitor);

    /**
     * Children of current node.
     *
     * @return Node's Collection of Visitable children.
     * @tparam <VISITABLE> type of base node.
     */
    Collection<VISITABLE> children();
  }

  /**
   * Agent Visitor - like the traditional Visitor but with more features.
   */
  public interface Agent<VISITABLE extends Target<VISITABLE,VISITOR,R>, VISITOR extends Agent<VISITABLE,VISITOR,R>, R> {

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
    default public BreadthControl<Pair<Function<?,R>, VISITABLE>> getBreadthControl() {
      return (BreadthControl<Pair<Function<?,R>, VISITABLE>>) BreadthControl.qcs.get();
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
     * The Filter used to determine if a node or a node's children should
     * be visited.
     * 
     * @return a Filter
     */
    default public Filter<VISITABLE> filter() {
      return new Filter<VISITABLE>(){ };
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
                                       VISITABLE node) {
      return newResult;
    }
    default public R mergeVisitTraversal(R currentResult,  
                                         R newResult, 
                                         VISITABLE node) {
      return newResult;
    }
    default public R mergeBreadth(R currentResult,  
                                  R newResult, 
                                  VISITABLE node) {
      return newResult;
    }
    default public R mergeTraversal(R currentResult,  
                                    R newResult, 
                                    VISITABLE node) {
      return newResult;
    }

    /** 
     * Default visit method for all nodes. Throws a
     * MissingImplementationException if the errorOnMissing returns true.
     * Does nothing otherwise.
     * 
     * @param node The Visit.Target VISITABLE node.
     */
    default public R visit(VISITABLE node) {
      debug("Agent.visit: node=" + node.getClass().getName());

      if (errorOnMissing()) {
        String msg = "Missing 'visit' methods for type: " +
                       node.getClass().getName();
        throw new MissingImplementationException(msg);
      }

      return null;
    }

    
    /** 
     * Orchestrates the visiting of a node and its children.
     *
     * See the "traverse" method for the orchestration of calling
     * a node's children.
     *
     * Note that the "accept" method called here is the Function
     * "accept" method and not the node Visit.Target Visitable's
     * "accept" method.
     * 
     * @param func 
     * @param node 
     */
    @SuppressWarnings("unchecked")
    default public R visit(VISITABLE node, Function<?,R> func) {
      debug("Agent.visit: TOP " +node.getClass().getName());

      R retval = null;
      R new_retval = null;

      if (filter().visitNode(node)) {
        switch (traversal()) {
          case BREADTH_FIRST:
            retval=breadth(node, func);
            break;
          case DEPTH_FIRST_PRE:
            new_retval=((Function<VISITABLE,R>) func).apply(node);
            retval=mergeVisitCurrent(retval, new_retval, node);
            new_retval=traverse(node);
            retval=mergeVisitTraversal(retval, new_retval, node);
            break;
          case DEPTH_FIRST_POST:
            new_retval=traverse(node);
            retval=mergeVisitTraversal(retval, new_retval, node);
            new_retval=((Function<VISITABLE,R>) func).apply(node);
            retval=mergeVisitCurrent(retval, new_retval, node);
            break;
          case DEPTH_FIRST_AROUND:
            new_retval=((Function<VISITABLE,R>) func).apply(node);
            retval=mergeVisitCurrent(retval, new_retval, node);
            break;
        }
      }

      debug("Agent.visit: BOTTOM " +node.getClass().getName());

      return retval;
    }

    @SuppressWarnings("unchecked")
    default public R breadth(VISITABLE node, Function<?,R> func) {
      BreadthControl<Pair<Function<?,R>,VISITABLE>> qc = getBreadthControl();
      qc.queue().enqueue(new Pair<Function<?,R>,VISITABLE>(func, node));

      R retval = null;
      R new_retval = null;

      if (qc.notInBreadth()) {
        qc.enterBreadth();
        try {
          while (! qc.queue().isEmpty()) {
            if (stop()) {
              break;
            }
            Pair<Function<?,R>,VISITABLE> pair = qc.queue().dequeue();
            Function<?,R> funcNext = pair.left;
            VISITABLE nodeNext = pair.right;
            new_retval=((Function<VISITABLE,R>) funcNext).apply(nodeNext);
            retval=mergeBreadth(retval, new_retval, nodeNext);
            if (filter().visitChildren(nodeNext)) {
              for (VISITABLE child : nodeNext.children()) {
                new_retval=child.accept((VISITOR)this);
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
    
    /** 
     * Orchestrates the visiting of a node's children.
     * The children are visited if the Visit.Filter permits it and
     * until the "stop" method returns true.
     * 
     * @param node 
     */
    @SuppressWarnings("unchecked")
    default public R traverse(VISITABLE node) {
      debug("Agent.traverse: TOP " +node.getClass().getName());

      R retval = null;
      R new_retval = null;

      if (filter().visitChildren(node)) {
        for (VISITABLE child : node.children()) {
          if (stop()) {
            break;
          }
          debug("Agent.traverse: call accept child=" +child.getClass().getName());
          new_retval=child.accept((VISITOR)this);
          retval=mergeTraversal(retval, new_retval, child);
        }
      }

      debug("Agent.traverse: BOTTOM " +node.getClass().getName());
      return retval;
    }
  }
}

