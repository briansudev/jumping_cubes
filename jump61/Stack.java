package jump61;

/** A simple linked list implementation of a stack
 * that supports push and pop operations. FIFO.
 * @author Brian Su and Sedgewick, Algorithms(4th ed.) */
public class Stack<T> {

    /** Initializes an empty stack. */
    public Stack() {
        _first = null;
    }

    /** The first node in the stack. */
    private Node _first;

    /** Node containing an element and reference to the next Node. */
    private class Node {
        /** Holds the item of the Node. */
        private T _item;
        /** Pointer to rest. */
        private Node _rest;

        /** Returns _ITEM. */
        T getItem() {
            return _item;
        }

        /** Sets _ITEM to OBJ. */
        void setItem(T obj) {
            _item = obj;
        }

        /** Returns _REST. */
        Node getRest() {
            return _rest;
        }

        /** Sets _REST to OBJ. */
        void setRest(Node obj) {
            _rest = obj;
        }
    }

    /** Removes and returns the first item from the stack. */
    public T pop() {
        T item = _first.getItem();
        _first = _first.getRest();
        return item;
    }

    /** Adds OBJ to stack. */
    public void push(T obj) {
        Node rest = _first;
        _first = new Node();
        _first.setItem(obj);
        _first.setRest(rest);
    }

    /** Returns True if stack is empty. */
    public boolean isEmpty() {
        return _first == null;
    }

}
