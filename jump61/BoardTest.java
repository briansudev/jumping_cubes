package jump61;

import static jump61.Color.*;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of Boards.
 *  @author Brian Su
 */
public class BoardTest {

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testSize() {
        Board B = new MutableBoard(5);
        assertEquals("bad length", 5, B.size());
        ConstantBoard C = new ConstantBoard(B);
        assertEquals("bad length", 5, C.size());
        Board D = new MutableBoard(C);
        assertEquals("bad length", 5, C.size());
    }

    @Test
    public void testSet() {
        Board B = new MutableBoard(5);
        B.set(2, 2, 1, RED);
        B.setMoves(1);
        assertEquals("wrong number of spots", 1, B.spots(2, 2));
        assertEquals("wrong color", RED, B.color(2, 2));
        assertEquals("wrong count", 1, B.numOfColor(RED));
        assertEquals("wrong count", 0, B.numOfColor(BLUE));
        assertEquals("wrong count", 24, B.numOfColor(WHITE));
    }

    @Test
    public void testMove() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 1, 1);
        checkBoard("#1", B, 1, 1, 1, RED);
        B.addSpot(BLUE, 2, 1);
        checkBoard("#2", B, 1, 1, 1, RED, 2, 1, 1, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("#3", B, 1, 1, 2, RED, 2, 1, 1, BLUE);
        B.addSpot(BLUE, 2, 1);
        checkBoard("#4", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("#5", B, 1, 1, 1, RED, 2, 1, 3, RED, 1, 2, 1, RED);
        B.undo();
        checkBoard("#4U", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.undo();
        checkBoard("#3U", B, 1, 1, 2, RED, 2, 1, 1, BLUE);
        B.undo();
        checkBoard("#2U", B, 1, 1, 1, RED, 2, 1, 1, BLUE);
        B.undo();
        checkBoard("#1U", B, 1, 1, 1, RED);
    }

    @Test
    public void testNumMoves() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 2, 2);
        B.addSpot(BLUE, 6, 6);
        assertEquals(B.numMoves(), 2);
        B.addSpot(RED, 2, 2);
        assertEquals(B.numMoves(), 3);
        B.addSpot(BLUE, 6, 6);
        assertEquals(B.numMoves(), 4);
    }

    @Test
    public void testRowCol() {
        Board B = new MutableBoard(5);
        assertEquals(B.row(3), 1);
        assertEquals(B.row(10), 3);
        assertEquals(B.row(5), 2);
        assertEquals(B.row(6), 2);
        assertEquals(B.col(14), 5);
        assertEquals(B.col(18), 4);
        assertEquals(B.col(24), 5);
        assertEquals(B.col(21), 2);
    }

    @Test
    public void testSqNum() {
        Board B = new MutableBoard(5);
        assertEquals(B.sqNum(1, 1), 0);
        assertEquals(B.sqNum(1, 2), 1);
        assertEquals(B.sqNum(2, 1), 5);
        assertEquals(B.sqNum(3, 1), 10);
        assertEquals(B.sqNum(5, 1), 20);
        assertEquals(B.sqNum(2, 2), 6);
        assertEquals(B.sqNum(2, 5), 9);
    }

    @Test
    public void testIsLegalWhoseMove() {
        Board B = new MutableBoard(6);
        B.set(2, 2, 4, RED);
        B.addSpot(RED, 2, 2);
        B.addSpot(BLUE, 6, 6);
        assertTrue("Red move", B.isLegal(RED));
        assertTrue("Not Blue's turn", !B.isLegal(BLUE));
        assertTrue(B.isLegal(RED, 6, 5));
        B.setMoves(1);
        assertTrue("Blue move", B.isLegal(BLUE));
        assertTrue("Not Red's turn", !B.isLegal(RED));
        assertTrue(B.isLegal(BLUE, 6, 6));
    }

    @Test
    public void testColor() {
        Board B = new MutableBoard(5);
        B.set(2, 2, 4, RED);
        B.addSpot(RED, 2, 2);
        assertEquals("wrong number of spots", 1, B.spots(2, 2));
        assertEquals("wrong color", RED, B.color(1, 2));
        assertEquals("wrong color", RED, B.color(2, 1));
        assertEquals("wrong color", RED, B.color(2, 2));
        assertEquals("wrong color", RED, B.color(2, 3));
        assertEquals("wrong color", RED, B.color(3, 2));
        assertEquals("wrong count", 5, B.numOfColor(RED));
        assertEquals("wrong count", 0, B.numOfColor(BLUE));
        assertEquals("wrong count", 20, B.numOfColor(WHITE));
    }

    @Test
    public void testNeighbors() {
        Board B = new MutableBoard(6);
        assertEquals("Wrong number of neighbors", 4, B.neighbors(2, 2));
        assertEquals("Wrong number of neighbors", 3, B.neighbors(1, 2));
        assertEquals("Wrong number of neighbors", 2, B.neighbors(1, 1));
        assertEquals("Wrong number of neighbors", 2, B.neighbors(35));
        assertEquals("Wrong number of neighbors", 3, B.neighbors(6));
        assertEquals("Wrong number of neighbors", 4, B.neighbors(9));
    }

    /** Check if board B has CONTENTS. If not, output MSG. */
    private void checkBoard(String msg, Board B, Object... contents) {
        for (int k = 0; k < contents.length; k += 4) {
            String M = String.format("%s at %d %d", msg, contents[k],
                                     contents[k + 1]);
            assertEquals(M, (int) contents[k + 2],
                         B.spots((int) contents[k], (int) contents[k + 1]));
            assertEquals(M, contents[k + 3],
                         B.color((int) contents[k], (int) contents[k + 1]));
        }
        int c;
        c = 0;
        for (int i = B.size() * B.size() - 1; i >= 0; i -= 1) {
            assertTrue("bad white square #" + i,
                       (B.color(i) == WHITE) == (B.spots(i) == 0));
            if (B.color(i) != WHITE) {
                c += 1;
            }
        }
        assertEquals("extra squares filled", contents.length / 4, c);
    }

}
