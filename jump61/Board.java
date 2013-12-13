package jump61;

import static jump61.Color.*;
import static jump61.GameException.error;

/** Represents the state of a Jump61 game.  Squares are indexed either by
 *  row and column (between 1 and size()), or by square number, numbering
 *  squares by rows, with squares in row 1 numbered 0 - size()-1, in
 *  row 2 numbered size() - 2*size() - 1, etc.
 *  @author Brian Su
 */
abstract class Board {

    /** (Re)initialize me to a cleared board with N squares on a side. Clears
     *  the undo history and sets the number of moves to 0. */
    void clear(int N) {
        unsupported("clear");
    }

    /** Copy the contents of BOARD into me. */
    void copy(Board board) {
        unsupported("copy");
    }

    /** Return the number of rows and of columns of THIS. */
    abstract int size();

    /** Returns the number of spots in the square at row R, column C,
     *  1 <= R, C <= size (). */
    abstract int spots(int r, int c);

    /** Returns the number of spots in square #N. */
    abstract int spots(int n);

    /** Returns the color of square #N, numbering squares by rows, with
     *  squares in row 1 number 0 - size()-1, in row 2 numbered
     *  size() - 2*size() - 1, etc. */
    abstract Color color(int n);

    /** Returns the color of the square at row R, column C,
     *  1 <= R, C <= size(). */
    abstract Color color(int r, int c);

    /** Returns the total number of moves made (red makes the odd moves,
     *  blue the even ones). */
    abstract int numMoves();

    /** Returns the square representation of the board. */
    abstract Square[] getSquares();

    /** Returns the Color of the player who would be next to move.  If the
     *  game is won, this will return the loser (assuming legal position). */
    Color whoseMove() {
        return (numMoves() % 2 == 0) ? RED : BLUE;
    }

    /** Return true iff row R and column C denotes a valid square. */
    final boolean exists(int r, int c) {
        return 1 <= r && r <= size() && 1 <= c && c <= size();
    }

    /** Return true iff S is a valid square number. */
    final boolean exists(int s) {
        int N = size();
        return 0 <= s && s < N * N;
    }

    /** Return the row number for square #N. */
    final int row(int n) {
        if (exists(n)) {
            return (int) (Math.floor(n / size())) + 1;
        } else {
            throw error("Row " + n + " does not exist.");
        }
    }

    /** Return the column number for square #N. */
    final int col(int n) {
        if (exists(n)) {
            return n % size() + 1;
        } else {
            throw error("Column " + n + " does not exist.");
        }
    }

    /** Return the square number of row R, column C. */
    final int sqNum(int r, int c) {
        if (exists(r, c)) {
            return (r - 1) * size() + c - 1;
        } else {
            throw error("Square of row and column does not exist");
        }
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
        to square at row R, column C. */
    boolean isLegal(Color player, int r, int c) {
        return isLegal(player, sqNum(r, c));
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
     *  to square #N. */
    boolean isLegal(Color player, int n) {
        boolean playable = player.playableSquare(getSquares()[n].getColor());
        return playable && isLegal(player);
    }

    /** Returns true iff PLAYER is allowed to move at this point. */
    boolean isLegal(Color player) {
        return whoseMove() == player;
    }

    /** Returns the winner of the current position, if the game is over,
     *  and otherwise null. */
    final Color getWinner() {
        if (numOfColor(RED) == size() * size()) {
            return RED;
        } else if (numOfColor(BLUE) == size() * size()) {
            return BLUE;
        } else {
            return null;
        }
    }

    /** Return the number of squares of given COLOR. */
    abstract int numOfColor(Color color);

    /** Add a spot from PLAYER at row R, column C.  Assumes
     *  isLegal(PLAYER, R, C). */
    void addSpot(Color player, int r, int c) {
        unsupported("addSpot");
    }

    /** Add a spot from PLAYER at square #N.  Assumes isLegal(PLAYER, N). */
    void addSpot(Color player, int n) {
        unsupported("addSpot");
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white).  Clear the undo
     *  history. */
    void set(int r, int c, int num, Color player) {
        unsupported("set");
    }

    /** Set the square #N to NUM spots (0 <= NUM), and give it color PLAYER
     *  if NUM > 0 (otherwise, white).  Clear the undo history. */
    void set(int n, int num, Color player) {
        unsupported("set");
    }

    /** Set the current number of moves to N.  Clear the undo history. */
    void setMoves(int n) {
        unsupported("setMoves");
    }

    /** Undo the effects one move (that is, one addSpot command).  One
     *  can only undo back to the last point at which the undo history
     *  was cleared, or the construction of this Board. */
    void undo() {
        unsupported("undo");
    }

    /** Returns my dumped representation. */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("===");
        for (int i = 0; i < size() * size(); i++) {
            if (i % size() == 0) {
                newLine(out);
                fourSpaces(out);
            }
            out.append(getSquares()[i].toString());
            if (i % size() == size() - 1) {
                continue;
            }
            out.append(" ");
        }
        newLine(out);
        out.append("===");
        return out.toString();
    }

    /** Appends a new line to StringBuilder OUT. */
    private void newLine(StringBuilder out) {
        out.append(System.getProperty("line.separator"));
    }

    /** Adds four spaces to StringBuilder OUT. */
    private void fourSpaces(StringBuilder out) {
        out.append("    ");
    }

    /** Returns an external rendition of me, suitable for
     *  human-readable textual display.  This is distinct from the dumped
     *  representation (returned by toString). */
    public String toDisplayString() {
        StringBuilder out = new StringBuilder();
        if (getSquares() != null) {
            out.append(getSquares()[0]);
            for (int i = 1; i < getSquares().length; i++) {
                out.append(" ");
                out.append(getSquares()[i].toString());
            }
        } else {
            out.append("The board has not been initialized.");
        }
        return out.toString();
    }

    /** Returns the number of neighbors of the square at row R, column C. */
    int neighbors(int r, int c) {
        int num = 4;
        if (r == size() || r == 1 || c == size() || c == 1) {
            num--;
            if ((r == 1 || r == size()) && (c == 1 || c == size())) {
                num--;
            }
        }
        return num;
    }

    /** Returns the number of neighbors of square #N. */
    int neighbors(int n) {
        return neighbors(row(n), col(n));
    }

    /** Indicate fatal error: OP is unsupported operation. */
    private void unsupported(String op) {
        String msg = String.format("'%s' operation not supported", op);
        throw new UnsupportedOperationException(msg);
    }

}
