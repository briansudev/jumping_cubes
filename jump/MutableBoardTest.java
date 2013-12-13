package jump61;

import static jump61.Color.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Mutable Board Unit Tests.
 * @author Brian Su
 **/
public class MutableBoardTest {

    /** Initiate stuff. */
    private void initialize() {
        _b = new MutableBoard(6);
    }

    @Test
    public void test() {
        initialize();
        _b.set(1, 1, 2, BLUE);
        _b.set(2, 2, 4, RED);
        assertEquals(_b.numOfColor(RED), 1);
        assertEquals(_b.numOfColor(BLUE), 1);
        _b.addSpot(RED, 2, 2);
        assertEquals(_b.numOfColor(RED), 5);
        _b.addSpot(BLUE, 1, 1);
        assertEquals(_b.numOfColor(BLUE), 3);
        assertEquals(_b.numOfColor(RED), 3);
        _b.undo();
        assertEquals(_b.numOfColor(RED), 5);
        _b.clear(6);
        assertEquals(_b.numOfColor(WHITE), 36);
    }

    /** Board. */
    Board _b;
}
