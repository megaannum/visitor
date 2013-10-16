
package com.megaannum.visitor.support;

import java.util.LinkedList;

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

