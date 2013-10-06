visitor
=======

Java8 visitor pattern.

Visitor is a design pattern. It first appeared in Mark Linton's C++ InterViews widget set (as did most of the patterns in the gang-of-four's design patterns book). A key feature of the Visitor pattern is its use of double-dispatch which enables the correct Visitor method to be called for each of the "visitable" elements.
Basically, if you have elements "a" of type A and "b" of type B, and the "visitor" instance of the type Visitor with methods:
    
    public void visit(A a) { ... }
    public void visit(B b) { ... }

then 

    visitor.visit(a);

will call the 

    public void visit(A a) { ... }

method and the call

    visitor.visit(b);

will call the 

    public void visit(B b) { ... }

method. Additionally, the elemnet types A and B have methods:

    class A {
      ...
      public void accept(Visitor visitor) {
        visitor.visit(this);
      }
    }
    class B {
      ...
      public void accept(Visitor visitor) {
        visitor.visit(this);
      }
    }

Nothing new here.

One of the problems with this is that it is hard to structure the pattern so that:

  Filters can be applied as to whether an element should be visited or not,
  Filters can be applied as to whether an element's children should be visited or not,
  Mechanism to stop the visiting after some condition is reached,
  Whether the element "visit" occurs before or after the element's children have been traversed,
  Whether the element "visit", itself, controls when the element's children are traversed, and, finally,
  If one is doing a breadth-first or depth-first traversal.

Yes, one can use generics to add a parameterized return value, say R, and a secondary argument of type, say D:

    public <R,D> R visit(A a, D data) { ... }
    public <R,D> R visit(B b, D data) { ... }

and

    class A {
      ...
      public <R,D> R accept(Visitor visitor, D data) {
        return visitor.visit(this, data);
      }
    }
    class B {
      ...
      public <R,D> R accept(Visitor visitor, D data) { 
        return visitor.visit(this, data);
      }
    }

but the Visitor control structure is still not addressed.

What is needed is a way to encapsulate the chosen method along with the element and pass the two to some standard method that then controls the flow.

This is exactly what this code does. It does so by using the new Java8 method
reference capability.


    import java.util.function.Consumer;

    class A implements Visit.Agent<A, Visitor> {
      ...
      public void accept(Visitor visitor) {
        Consumer<A> func = visitor::visit
        visit(func, this);
      }
    }
    class B implements Visit.Agent<B, Visitor> {
      ...
      public void accept(Visitor visitor) {
        Consumer<B> func = visitor::visit
        visit(func, this);
      }
    }

The interface, Visit.Agent, has the default method

    @SuppressWarnings("unchecked")
    default public void visit(Consumer<?> func, VISITABLE node) {
      if (filter().visitNode(node)) {
        switch (traversal()) {
          case BREADTH_FIRST:
            breadth(func, node);
            break;
          case DEPTH_FIRST_PRE:
            ((Consumer<VISITABLE>) func).accept(node);
            traverse(node, data);
            break;
        case DEPTH_FIRST_POST:
            traverse(node, data);
            ((Consumer<VISITABLE>) func).accept(node);
            break;
        case DEPTH_FIRST_AROUND:
            ((Consumer<VISITABLE>) func).accept(node);
            break;
        }
      }
    }

which is called with the method reference. The Consumer class has an "accept" (not to be confused with the Visitable element "accept" method) which is called in the case of depth-first traversal: before (DEPTH_FIRST_PRE) the child traversal, after (DEPTH_FIRST_POST) the child traversal or simply called directly in which case it is up to the particular Visitor "visit" method to call the child traversal if required, and in the case of breadth-first traversals (BREADTH_FIRST) is called as it is removed from the Queue (see implementation).

Note that the above also has a filter which is used to determine if the "visit" method associated with the element should be called or not.

The Visit.Agent "traverse" method is given below

    @SuppressWarnings("unchecked")
    default public <D> void traverse(VISITABLE node, D data) {
      if (filter().visitChildren(node)) {
        for (VISITABLE child : node.children()) {
          if (stop()) {
            break;
          }
          child.accept((VISITOR)this, data);
        }
      }
    }

This method control the element's child traversal. It has a filter and
checks it the Visitor should stop or not.

The "breadth" method orchestrates a breadth-first traversal. While, I am not saying that its pretty, it does work. The QueueControl is gotten from the Visit.Agent interface (yes, I have an Object associated with and Interface) which supports the traversal and holds the breadth-first Queue (pretty standard way of doing such a traversal). The method controls both the enqueuing of nodes and their dequeuing, calling their associated "visit" method and adding their children to the queue.

The full implementation is under the "src" directory in the Visit.java file. The Visit interface contains three inner interfaces: 

  Filter: the filter interface with default implementations,
  Target: the visitable interface which all Element to be visited must implement which as the standard Visitor "accept" method as well as a "children" method that returns a Collection of child elements, and
  Agent: the standard visitor base interface which has a number of default methods that control the visit traversal.

There is an "example" directory that can be build and run using "run_example" which demonstrates all four types of traversals.
