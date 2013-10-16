
package com.megaannum.visitor.std.biconsumer;

import com.megaannum.visitor.support.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiConsumer;

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
  public interface Target<VISITABLE extends Target<VISITABLE,VISITOR>, VISITOR extends Agent<VISITABLE,VISITOR>> {

    /**
     * Accept method used to implement the visitor pattern.  
     *
     * @param visitor Visitor instance.
     */
    <D> void accept(VISITOR visitor, D data);

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
  public interface Agent<VISITABLE extends Target<VISITABLE,VISITOR>, VISITOR extends Agent<VISITABLE,VISITOR>> {

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
    default public <D> BreadthControl<Pair<BiConsumer<?,D>, VISITABLE>> getBreadthControl() {
      return (BreadthControl<Pair<BiConsumer<?,D>, VISITABLE>>) BreadthControl.qcs.get();
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
     * The Filter used to determine if a node or a node's children should
     * be visited.
     * 
     * @return a BiFilter
     */
    default public BiFilter<VISITABLE> filter() {
      return new BiFilter<VISITABLE>(){ };
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
     * @param node The Visit.Target VISITABLE node.
     */
    default public <D> void visit(VISITABLE node, D data) {
      debug("Agent.visit: node=" + node.getClass().getName());

      if (errorOnMissing()) {
        String msg = "Missing 'visit' methods for type: " +
                       node.getClass().getName();
        throw new MissingImplementationException(msg);
      }
    }

    
    /** 
     * Orchestrates the visiting of a node and its children.
     *
     * See the "traverse" method for the orchestration of calling
     * a node's children.
     *
     * Note that the "accept" method called here is the BiConsumer
     * "accept" method and not the node Visit.Target Visitable's
     * "accept" method.
     * 
     * @param func 
     * @param node 
     */
    @SuppressWarnings("unchecked")
    default public <D> void visit(VISITABLE node, D data, BiConsumer<?,D> func) {
      debug("Agent.visit: TOP " +node.getClass().getName());

      if (filter().visitNode(node, data)) {
        switch (traversal()) {
          case BREADTH_FIRST:
            breadth(node, data, func);
            break;
          case DEPTH_FIRST_PRE:
            ((BiConsumer<VISITABLE,D>) func).accept(node, data);
            traverse(node, data);
            break;
          case DEPTH_FIRST_POST:
            traverse(node, data);
            ((BiConsumer<VISITABLE,D>) func).accept(node, data);
            break;
          case DEPTH_FIRST_AROUND:
            ((BiConsumer<VISITABLE,D>) func).accept(node, data);
            break;
        }
      }

      debug("Agent.visit: BOTTOM " +node.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    default public <D> void breadth(VISITABLE node, D data, BiConsumer<?,D> func) {
      BreadthControl<Pair<BiConsumer<?,D>,VISITABLE>> qc = getBreadthControl();
      qc.queue().enqueue(new Pair<BiConsumer<?,D>,VISITABLE>(func, node));

      if (qc.notInBreadth()) {
        qc.enterBreadth();
        try {
          while (! qc.queue().isEmpty()) {
            if (stop()) {
              break;
            }
            Pair<BiConsumer<?,D>,VISITABLE> pair = qc.queue().dequeue();
            BiConsumer<?,D> funcNext = pair.left;
            VISITABLE nodeNext = pair.right;
            ((BiConsumer<VISITABLE,D>) funcNext).accept(nodeNext, data);
            if (filter().visitChildren(nodeNext, data)) {
              for (VISITABLE child : nodeNext.children()) {
                child.accept((VISITOR)this, data);
              }
            }
          }
        } finally {
          qc.leaveBreadth();
        }
      }
    }
    
    /** 
     * Orchestrates the visiting of a node's children.
     * The children are visited if the Visit.Filter permits it and
     * until the "stop" method returns true.
     * 
     * @param node 
     */
    @SuppressWarnings("unchecked")
    default public <D> void traverse(VISITABLE node, D data) {
      debug("Agent.traverse: TOP " +node.getClass().getName());

// http://stackoverflow.com/questions/5262308/how-do-implement-a-breadth-first-traversal

      if (filter().visitChildren(node, data)) {
        for (VISITABLE child : node.children()) {
          if (stop()) {
            break;
          }
          debug("Agent.traverse: call accept child=" +child.getClass().getName());
          child.accept((VISITOR)this, data);
        }
      }
      debug("Agent.traverse: BOTTOM " +node.getClass().getName());
    }
  }
}

