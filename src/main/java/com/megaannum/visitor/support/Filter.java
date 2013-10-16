
package com.megaannum.visitor.support;

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

