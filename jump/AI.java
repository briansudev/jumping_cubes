package jump61;

import java.util.ArrayList;

/** An automated Player.
 *  @author Brian Su
 */
class AI extends Player {

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Color color) {
        super(game, color);
        _game = game;
        _color = color;
    }

    /** Initialize the board. */
    void initialize() {
        _board = _game.getBoard();
        _numSquares = _game.getBoard().getSquares().length;
    }

    @Override
    void makeMove() {
        initialize();
        int[] move = findBestMove(_color, _board, 4, Integer.MAX_VALUE);
        _game.moveAI(move[1]);
    }

    /** Returns an array containg the move value and move of the best move for
     * player WHO in board START with DEPTH and CUTOFF. */
    private int[] findBestMove(Color who, Board start,
            int depth, double cutoff) {
        ArrayList<Integer> moves = generateMoves(who);
        if (_board.getWinner() == who) {
            return new int[] {Integer.MAX_VALUE, 4};
        } else if (_board.getWinner() == who.opposite()) {
            return new int[] {-Integer.MAX_VALUE + 1, 8};
        } else if (depth == 0) {
            int[] gbm = guessBestMove(who, moves);
            return gbm;
        }

        int[] bestSoFar = new int[] {-Integer.MAX_VALUE, -1};
        for (int move : moves) {
            _board.addSpot(who, move);
            int[] moveInfo = {staticEval(who), move};
            int[] response = findBestMove(who.opposite(),
                    _board, depth - 1, -bestSoFar[0]);
            _board.undo();
            if (-response[0] > bestSoFar[0]) {
                moveInfo[0] = -response[0];
                bestSoFar = moveInfo;
                if (moveInfo[0] >= cutoff) {
                    break;
                }
            }
        }
        return bestSoFar;
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private int staticEval(Color p) {
        if (_board.getWinner() != null) {
            if (_board.getWinner() == p) {
                return Integer.MAX_VALUE;
            } else {
                return -Integer.MAX_VALUE;
            }
        } else {
            return _board.numOfColor(p) - _board.numOfColor(p.opposite());
        }
    }

    /** Returns a list of all possible moves for player P in the current
     *  board. */
    private ArrayList<Integer> generateMoves(Color p) {
        ArrayList<Integer> possibleMoves = new ArrayList<Integer>();
        for (int i = 0; i < _numSquares; i++) {
            if (_board.isLegal(p, i)) {
                possibleMoves.add(i);
            }
        }
        return possibleMoves;
    }

    /** Returns the best move out of the possible MOVES for P. */
    private int[] guessBestMove(Color p, ArrayList<Integer> moves) {
        int move, moveValue;
        move = moves.get(0);
        moveValue = -Integer.MAX_VALUE;
        for (int aMove : moves) {
            _board.addSpot(p, aMove);
            int aMoveValue = staticEval(p);
            if (aMoveValue > moveValue) {
                move = aMove;
                moveValue = aMoveValue;
            }
            _board.undo();
        }
        return new int[] {moveValue, move};
    }

    /** Game. */
    private Game _game;
    /** Board. */
    private Board _board;
    /** Color. */
    private Color _color;
    /** Number of squares. */
    private int _numSquares;
}


