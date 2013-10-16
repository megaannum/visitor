
package com.megaannum.visitor.support;

/** 
 * Utility class that provides the Queue required to do a breadth-first
 * traversal as well as helps with the breadth-first flow control.
 *
 * Note that this is one-per-thread which means that one can NOT
 * have two breadth visitors active at the same time in the same thread.
 * 
 * @author Richard M. Emberson
 * @since Oct 06 2013
 */
public class BreadthControl<Q> {

  public static final ThreadLocal<BreadthControl<?>> qcs = new ThreadLocal<BreadthControl<?>>() {
    @Override
    protected BreadthControl<?> initialValue() {
      return new BreadthControl<Object>();
    }
  };

  private final Queue<Q> queue;
  private boolean inBreath;

  public BreadthControl() {
    this.queue = new Queue<Q>();
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

    // clear from ThreadLocal
    qcs.remove();
  }

  public Queue<Q> queue() {
    return queue;
  }
}

