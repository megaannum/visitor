
package com.megaannum.visitor.support;

/**
 * Filter Visitable Objects during traversal.
 */
public interface BiFilter<VISITABLE> {
  
  /** 
   * Should visit this Visitable node or not.
   * 
   * @param node Visitable node.
   * @return true to visit node and false otherwise.
   */
  default <D> boolean visitNode(VISITABLE node, D data) {
    return true;
  }
  
  /** 
   * Should visit this Visitable node's childern or not.
   * 
   * @param node Visitable node.
   * @return true to visit node's children and false otherwise.
   */
  default <D> boolean visitChildren(VISITABLE node, D data) {
    return true;
  }
}

