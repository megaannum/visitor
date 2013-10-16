
package com.megaannum.visitor.support;

/** 
 * Simple Two-Tuple. 
 * 
 * @author Richard M. Emberson
 * @since Oct 06 2013
 */
public class Pair<LEFT,RIGHT> {
  public final LEFT left;
  public final RIGHT right;
  public Pair(LEFT left, RIGHT right) {
    this.left = left;
    this.right = right;
  }
}

