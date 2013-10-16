
package reflect.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

abstract class PolyNode implements Node {

  protected List<Node> list = new ArrayList<Node>();

  public Collection<Node> children() {
    return list;
  }
}

