
package com.megaannum.visitor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

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
   * Filter Visitable Objects during traversal.
   */
  public interface Filter<VISITABLE> {
    
    /** 
     * Should visit this Visitable node or not.
     * 
     * @param node Visitable node.
     * @return true to visit node and false otherwise.
     */
    default boolean visitNode(VISITABLE node) {
      return true;
    }
    
    /** 
     * Should visit this Visitable node's childern or not.
     * 
     * @param node Visitable node.
     * @return true to visit node's children and false otherwise.
     */
    default boolean visitChildren(VISITABLE node) {
      return true;
    }
  }

  /** 
   * Simple Two-Tuple. 
   * 
   * @author Richard M. Emberson
   * @since Oct 06 2013
   */
  public class Pair<LEFT,RIGHT> {
    final LEFT left;
    final RIGHT right;
    public Pair(LEFT left, RIGHT right) {
      this.left = left;
      this.right = right;
    }
  }
  
  /** 
   * Wrapper around a LinkedList with a simple Queue api. 
   */
  public class Queue<E> {
    protected LinkedList<E> ll;
    public Queue() {
      this.ll = new LinkedList<E>();
    }
    public boolean isEmpty() {
      return ll.isEmpty();
    }
    public void enqueue(E e) {
      ll.addLast(e);
    }
    public E dequeue() {
      return ll.removeFirst();
    }
  }

  /** 
   * Utility class that provides the Queue required to do a breadth-first
   * traversal as well as helps with the breadth-first flow control.
   * 
   * @author Richard M. Emberson
   * @since Oct 06 2013
   */
  public class QueueControl<F,V> {

    public static final ThreadLocal<QueueControl<?,?>> qcs = new ThreadLocal<QueueControl<?,?>>() {
      @Override
      protected QueueControl<?,?> initialValue() {
        return new QueueControl<Object,Object>();
      }
    };



    private final Queue<Pair<F,V>> queue;
    private boolean inBreath;

    public QueueControl() {
      this.queue = new Queue<Pair<F,V>>();
      this.inBreath = false;
    }

    public boolean notInBreadth() {
      return ! inBreath;
    }
    public void enterBreadth() {
      inBreath = true;
    }
    public void leaveBreadth() {
      inBreath = false;
    }


    public Queue<Pair<F,V>> queue() {
      return queue;
    }
  }

  /**
   * Target Visitable Objects 
   */
  public interface Target<VISITABLE extends Target<VISITABLE,VISITOR>, VISITOR extends Agent<VISITABLE,VISITOR>> {

    /**
     * Accept method used to implement the visitor pattern.  
     *
     * @param visitor Visitor instance.
     */
    void accept(VISITOR visitor);

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
     * Traversal control: 
     *   DEPTH_FIRST_PRE     visit node prior to traversing children.
     *   DEPTH_FIRST_POST    visit node after to traversing children.
     *   DEPTH_FIRST_AROUND  visit node and its up to the node if/when traverse children.
     *   BREADTH_FIRST       visit nodes in a breadth first ordering
     */
    public enum Traversal {
      DEPTH_FIRST_PRE,
      DEPTH_FIRST_POST,
      DEPTH_FIRST_AROUND,
      BREADTH_FIRST
    };

    /** 
     * Gets the current Traversal value; 
     * 
     * @return traversal value.
     */
    Traversal traversal();

    /** 
     * This returns the default QueueControl mechanism which used a ThreadLocal.
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
    default public QueueControl<Consumer<?>, VISITABLE> getQueueControl() {
      return (QueueControl<Consumer<?>, VISITABLE>) QueueControl.qcs.get();
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
     * The Filter used to determine if a node or a node's children should
     * be visited.
     * 
     * @return a Visit.Filter
     */
    default public Visit.Filter<VISITABLE> filter() {
      return new Visit.Filter<VISITABLE>(){ };
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
    default public void visit(VISITABLE node) {
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
     * Note that the "accept" method called here is the Consumer
     * "accept" method and not the node Visit.Target Visitable's
     * "accept" method.
     * 
     * @param func 
     * @param node 
     */
    @SuppressWarnings("unchecked")
    default public void visit(Consumer<?> func, VISITABLE node) {
      debug("Agent.visit: TOP " +node.getClass().getName());

      if (filter().visitNode(node)) {
        switch (traversal()) {
          case BREADTH_FIRST:
            breadth(func, node);
            break;
          case DEPTH_FIRST_PRE:
            ((Consumer<VISITABLE>) func).accept(node );
            traverse(node);
            break;
          case DEPTH_FIRST_POST:
            traverse(node);
            ((Consumer<VISITABLE>) func).accept(node);
            break;
          case DEPTH_FIRST_AROUND:
            ((Consumer<VISITABLE>) func).accept(node);
            break;
        }
      }

      debug("Agent.visit: BOTTOM " +node.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    default public void breadth(Consumer<?> func, VISITABLE node) {
      QueueControl<Consumer<?>,VISITABLE> qc = getQueueControl();
      qc.queue().enqueue(new Pair<Consumer<?>,VISITABLE>(func, node));

      if (qc.notInBreadth()) {
        qc.enterBreadth();
        while (! qc.queue().isEmpty()) {
          if (stop()) {
            break;
          }
          Pair<Consumer<?>,VISITABLE> pair = qc.queue().dequeue();
          Consumer<?> funcNext = pair.left;
          VISITABLE nodeNext = pair.right;
          ((Consumer<VISITABLE>) funcNext).accept(nodeNext);
          if (filter().visitChildren(nodeNext)) {
            for (VISITABLE child : nodeNext.children()) {
              child.accept((VISITOR)this);
            }
          }
        }
        qc.leaveBreadth();
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
    default public void traverse(VISITABLE node) {
    debug("Agent.traverse: TOP " +node.getClass().getName());

// http://stackoverflow.com/questions/5262308/how-do-implement-a-breadth-first-traversal

    if (filter().visitChildren(node)) {
      for (VISITABLE child : node.children()) {
        if (stop()) {
          break;
        }
        debug("Agent.traverse: call accept child=" +child.getClass().getName());
        child.accept((VISITOR)this);
      }
    }
    debug("Agent.traverse: BOTTOM " +node.getClass().getName());
    }
  }
}

