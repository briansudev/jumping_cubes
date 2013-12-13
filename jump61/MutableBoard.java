package jump61;

import static jump61.Color.*;
import static jump61.GameException.error;

/** A Jump61 board state.
 *  @author Brian Su
 */
class MutableBoard extends Board {

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        initiate(N);
    }

    /** A board whose initial contents are copied from BOARD0. Clears the
     *  undo history. */
    MutableBoard(Board board0) {
        copy(board0);
        _undos = new Stack<Square[]>();
    }

    /** Create a new board of size N. */
    void initiate(int N) {
        _N = N;
        _moves = 0;
        _squares = new Square[N * N];
        populateSquares();
        _undos = new Stack<Square[]>();
    }

    /** Instantiate a Square object for every element in _SQUARES. */
    void populateSquares() {
        for (int i = 0; i < _squares.length; i++) {
            _squares[i] = new Square();
        }
    }

    @Override
    void clear(int N) {
        initiate(N);
    }

    @Override
    void copy(Board board) {
        _squares = board.getSquares();
    }

    @Override
    int size() {
        return _N;
    }

    @Override
    int spots(int r, int c) {
        return spots(sqNum(r, c));
    }

    @Override
    int spots(int n) {
        return _squares[n].getSpots();
    }

    @Override
    Color color(int r, int c) {
        return color(sqNum(r, c));
    }

    @Override
    Color color(int n) {
        return _squares[n].getColor();
    }

    @Override
    int numMoves() {
        return _moves;
    }

    @Override
    int numOfColor(Color color) {
        int num = 0;
        for (Square square : _squares) {
            if (square.getColor() == color) {
                num++;
            }
        }
        return num;
    }

    @Override
    void addSpot(Color player, int r, int c) {
        addSpot(player, sqNum(r, c));
    }

    @Override
    void addSpot(Color player, int n) {
        if (isLegal(player, n)) {
            _undos.push(copyArray(_squares));
            set(n, _squares[n].getSpots() + 1, player);
            _moves++;
            jump(n, player);
        } else {
            throw error("invalid move: %d %d", row(n), col(n));
        }
    }

    /** Returns a new Square array with contents of ORIGINAL.*/
    Square[] copyArray(Square[] original) {
        Square[] result = new Square[original.length];
        for (int i = 0; i < original.length; i++) {
            Square k = original[i];
            result[i] = new Square(k.getColor(), k.getSpots());
        }
        return result;
    }

    @Override
    void set(int r, int c, int num, Color player) {
        set(sqNum(r, c), num, player);
    }

    @Override
    void set(int n, int num, Color player) {
        if (num == 0) {
            player = WHITE;
        }
        _squares[n].setColor(player);
        _squares[n].setSpots(num);
    }

    @Override
    void setMoves(int num) {
        assert num > 0;
        _moves = num;
    }

    @Override
    void undo() {
        if (_moves > 0) {
            Square[] item = _undos.pop();
            _squares = item;
            _moves--;
        }
    }

    /** Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. Changes color of square to PLAYER. */
    private void jump(int S, Color player) {
        if (getWinner() != null) {
            return;
        }
        if (_squares[S].getSpots() > neighbors(S)) {
            _squares[S].setSpots(1);
            if (col(S) < size()) {
                _squares[S + 1].setSpots(_squares[S + 1].getSpots() + 1);
                changeColor(S + 1, player);
                jump(S + 1, player);
            }
            if (col(S) > 1) {
                _squares[S - 1].setSpots(_squares[S - 1].getSpots() + 1);
                changeColor(S - 1, player);
                jump(S - 1, player);
            }
            int n = size();
            if (row(S) < size()) {
                _squares[S + n].setSpots(_squares[S + n].getSpots() + 1);
                changeColor(S + n, player);
                jump(S + n, player);
            }
            if (row(S) > 1) {
                _squares[S - n].setSpots(_squares[S - n].getSpots() + 1);
                changeColor(S - n, player);
                jump(S - n, player);
            }
        }
    }

    /** Change the color of square N to COLOR if it is not already. */
    private void changeColor(int n, Color color) {
        if (_squares[n].getColor() != color) {
            _squares[n].setColor(color);
        }
    }

    /** Returns the array of squares. */
    public Square[] getSquares() {
        return _squares;
    }

    /** Total combined number of moves by both sides. */
    protected int _moves;
    /** Convenience variable: size of board (squares along one edge). */
    private int _N;
    /** Stack for storing undos. */
    private Stack<Square[]> _undos;
    /** Current board. */
    private Square[] _squares;
}
