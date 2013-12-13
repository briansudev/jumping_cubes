package jump61;

import static jump61.Color.*;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit Tests for my Stack implementation.
 * @author Brian Su
 **/
public class StackTest {

    /** Initialize stuff. */
    private void initialize() {
        _stack = new Stack<Integer>();
    }

    @Test
    public void testPushPop() {
        initialize();
        _stack.push(0);
        _stack.push(1);
        _stack.push(2);
        _stack.push(3);
        _stack.push(4);
        assertTrue(_stack.pop() == 4);
        assertTrue(_stack.pop() == 3);
        assertTrue(_stack.pop() == 2);
        assertTrue(_stack.pop() == 1);
        assertTrue(_stack.pop() == 0);
        assertTrue(_stack.isEmpty());
    }

    /** Stacks. */
    private Stack<Integer> _stack;
}
