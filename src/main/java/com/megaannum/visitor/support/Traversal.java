
package com.megaannum.visitor.support;


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
}

