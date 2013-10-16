
package com.megaannum.visitor.support;

/** 
 * Simple Three-Tuple. 
 * 
 * @author Richard M. Emberson
 * @since Oct 06 2013
 */
public class Triple<LEFT,MIDDLE,RIGHT> {
  public final LEFT left;
  public final MIDDLE middle;
  public final RIGHT right;
  public Triple(LEFT left, MIDDLE middle, RIGHT right) {
    this.left = left;
    this.middle = middle;
    this.right = right;
  }
}

