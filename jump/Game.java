package jump61;

import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;

import java.util.Scanner;
import java.util.Random;

import static jump61.Color.*;
import static jump61.GameException.error;

/** Main logic for playing (a) game(s) of Jump61.
 *  @author Brian Su
 */
class Game {

    /** Name of resource containing help message. */
    private static final String HELP = "jump61/Help.txt";

    /** A new Game that takes command/move input from INPUT, prints
     *  normal output on OUTPUT, prints prompts for input on PROMPTS,
     *  and prints error messages on ERROROUTPUT. The Game now "owns"
     *  INPUT, PROMPTS, OUTPUT, and ERROROUTPUT, and is responsible for
     *  closing them when its play method returns. */
    Game(Reader input, Writer prompts, Writer output, Writer errorOutput) {
        _board = new MutableBoard(Defaults.BOARD_SIZE);
        _prompter = new PrintWriter(prompts, true);
        _inp = new Scanner(input);
        _inp.useDelimiter("(?m)\\p{Blank}*$|^\\p{Blank}*|\\p{Blank}+");
        _out = new PrintWriter(output, true);
        _err = new PrintWriter(errorOutput, true);

        _redPlayer = new HumanPlayer(this, RED);
        _bluePlayer = new AI(this, BLUE);
    }

    /** Returns a view of the game board.  This board remains valid
     *  throughout the session. */
    Board getBoard() {
        return _board;
    }

    /** Play a session of Jump61.  This may include multiple games,
     *  and proceeds until the user exits.  Returns an exit code: 0 is
     *  normal; any positive quantity indicates an error.  */
    int play() {
        _out.println("Welcome to " + Defaults.VERSION);
        _out.flush();
        do {
            try {
                if (_playing) {
                    currentPlayer().makeMove();
                    checkForWin();
                } else if (promptForNext()) {
                    readExecuteCommand();
                } else {
                    _exitCode = 0;
                    break;
                }
            } catch (GameException e) {
                reportError(e.getMessage());
            }
        } while (_exitCode < 0);
        close();
        return _exitCode;
    }

    /** Closes all the PrintWriters. */
    void close() {
        _out.close();
        _err.close();
        _prompter.close();
    }

    /** Get a move from prompt and execute. */
    void getMove() {
        if (_playing && promptForNext()) {
            readExecuteCommand();
        }
    }

    /** Make a move.*/
    void move() {
        makeMove(_move[0], _move[1]);
    }

    /** Used for AI to make a move of N. */
    void moveAI(int n) {
        _out.printf("%s moves %d %d.%n", current().toCapitalizedString(),
                _board.row(n), _board.col(n));
        makeMove(n);
    }

    /** Add a spot to R C, if legal to do so. */
    void makeMove(int r, int c) {
        makeMove(_board.sqNum(r, c));
    }

    /** Add a spot to square #N, if legal to do so. */
    void makeMove(int n) {
        _board.addSpot(current(), n);
    }

    /** Returns the current player color. */
    Color current() {
        return _board.whoseMove();
    }

    /** Returns the current player. */
    Player currentPlayer() {
        if (current() == RED) {
            return _redPlayer;
        } else {
            return _bluePlayer;
        }
    }

    /** Return a random integer in the range [0 .. N), uniformly
     *  distributed.  Requires N > 0. */
    int randInt(int n) {
        return _random.nextInt(n);
    }

    /** Check whether we are playing and there is an unannounced winner.
     *  If so, announce and stop play. */
    private void checkForWin() {
        boolean winnerExists = _board.getWinner() != null;
        if (_playing && winnerExists) {
            announceWinner();
            stopPlay();
        }
    }

    /** Stops playing the game. */
    private void stopPlay() {
        _playing = false;
    }

    /** Start playing the game. */
    private void startPlay() {
        _playing = true;
    }

    /** Send announcement of winner to my user output. */
    private void announceWinner() {
        _out.println(current().opposite().toCapitalizedString() + " wins.");
    }

    /** Make PLAYER an AI for subsequent moves. */
    private void setAuto(Color player) {
        if (player == RED) {
            _redPlayer = new AI(this, player);
        } else {
            _bluePlayer = new AI(this, player);
        }
    }

    /** Make PLAYER take manual input from the user for subsequent moves. */
    private void setManual(Color player) {
        if (player == RED) {
            _redPlayer = new HumanPlayer(this, player);
        } else {
            _bluePlayer = new HumanPlayer(this, player);
        }
    }

    /** Stop any current game and clear the board to its initial
     *  state. */
    private void clear() {
        stopPlay();
        _board.clear(_board.size());
    }

    /** Print the current board using standard board-dump format. */
    private void dump() {
        _out.println(_board);
    }

    /** Print a help message. */
    private void help() {
        Main.printHelpResource(HELP, _out);
    }

    /** Stop any current game and set the move number to N. */
    private void setMoveNumber(int n) {
        stopPlay();
        _board.setMoves(n);
    }

    /** Seed the random-number generator with SEED. */
    private void setSeed(long seed) {
        _random.setSeed(seed);
    }

    /** Place SPOTS spots on square R:C and color the square red or
     *  blue depending on whether COLOR is "r" or "b".  If SPOTS is
     *  0, clears the square, ignoring COLOR.  SPOTS must be less than
     *  the number of neighbors of square R, C. */
    private void setSpots(int r, int c, int spots, String color) {
        if (spots >= 0 && spots <= _board.neighbors(r, c)) {
            _board.set(r, c, spots, Color.parseColor(color));
        } else {
            throw error("syntax error in 'set' command");
        }
    }

    /** Stop any current game and set the board to an empty N x N board
     *  with numMoves() == 0.  */
    private void setSize(int n) {
        stopPlay();
        _board.clear(n);
    }

    /** Begin accepting moves for game.  If the game is won,
     *  immediately print a win message and end the game. */
    private void restartGame() {
        startPlay();
        checkForWin();
    }

    /** Exit the game. */
    private void quit() {
        _exitCode = 0;
    }

    /** Save move R C in _move.  Error if R and C do not indicate an
     *  existing square on the current board. */
    private void saveMove(int r, int c) {
        if (!_board.exists(r, c)) {
            throw error("move %d %d out of bounds", r, c);
        }
        _move[0] = r;
        _move[1] = c;
    }

    /** Read and execute one command.  Leave the input at the start of
     *  a line, if there is more input. */
    private void readExecuteCommand() {
        String command = _inp.nextLine();
        String[] cmnd = command.trim().split("\\s+");
        if (intArg(cmnd[0])) {
            if (cmnd.length == 2) {
                try {
                    saveMove(parseInt(cmnd[0]), parseInt(cmnd[1]));
                    move();
                } catch (NumberFormatException e) {
                    throw error("syntax error in '<move>' command");
                }
            } else {
                throw error("syntax error in '<move>' command");
            }
        } else {
            executeCommand(cmnd);
        }
    }

    /** Return true if ARG is an integer. */
    private boolean intArg(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Returns the Integer.parseInt(S). */
    private int parseInt(String s) {
        return Integer.parseInt(s);
    }

    /** Throw an error of COMMAND if CMND is not of size N. */
    private void assertSize(String[] cmnd, int n, String command) {
        if (cmnd.length < n) {
            throw error("syntax error in '%s' command", command);
        }
    }

    /** Gather arguments cmnd and execute command CMND.  Throws GameException
     *  on errors. Ignores first element of array, which is cmnd. */
    private void executeCommand(String[] cmnd) {
        switch (cmnd[0]) {
        case "\n": case "\r\n":
            return;
        case "#":
            break;
        case "undo":
            _board.undo();
            break;
        case "clear":
            clear();
            break;
        case "start":
            restartGame();
            break;
        case "quit":
            quit();
            break;
        case "auto":
            assertSize(cmnd, 2, cmnd[0]);
            stopPlay();
            setAuto(parseColor(cmnd[1]));
            break;
        case "manual":
            assertSize(cmnd, 2, cmnd[0]);
            stopPlay();
            setManual(parseColor(cmnd[1]));
            break;
        case "size":
            assertSize(cmnd, 2, cmnd[0]);
            setSize(parseInt(cmnd[1]));
            break;
        case "move":
            assertSize(cmnd, 2, cmnd[0]);
            setMoveNumber(parseInt(cmnd[1]) + 1);
            break;
        case "set":
            assertSize(cmnd, 5, cmnd[0]);
            stopPlay();
            setSpots(parseInt(cmnd[1]), parseInt(cmnd[2]),
                    parseInt(cmnd[3]), cmnd[4]);
            break;
        case "dump":
            dump();
            break;
        case "seed":
            assertSize(cmnd, 2, cmnd[0]);
            setSeed(parseInt(cmnd[1]));
            break;
        case "help":
            help();
            break;
        default:
            throw error("bad command: '%s'", cmnd[0]);
        }
    }

    /** Print a prompt and wait for input. Returns true iff there is another
     *  token. */
    private boolean promptForNext() {
        if (_playing) {
            _prompter.print(current());
        }
        _prompter.print("> ");
        _prompter.flush();
        return _inp.hasNextLine();
    }

    /** Send an error message to the user formed from arguments FORMAT
     *  and ARGS, whose meanings are as for printf. */
    void reportError(String format, Object... args) {
        _err.print("Error: ");
        _err.printf(format, args);
        _err.println();
    }

    /** Sets the error code to 1. */
    void setError() {
        _exitCode = 1;
    }

    /** Returns the error code. (Testing). */
    int getError() {
        return _exitCode;
    }

    /** Writer on which to print prompts for input. */
    private final PrintWriter _prompter;
    /** Scanner from current game input.  Initialized to return
     *  newlines as tokens. */
    private final Scanner _inp;
    /** Outlet for responses to the user. */
    private final PrintWriter _out;
    /** Outlet for error responses to the user. */
    private final PrintWriter _err;

    /** The board on which I record all moves. */
    private final Board _board;

    /** A pseudo-random number generator used by players as needed. */
    private final Random _random = new Random();

    /** True iff a game is currently in progress. */
    private boolean _playing;

    /** Reference to RED player. */
    private Player _redPlayer;
    /** Reference to BLUE player. */
    private Player _bluePlayer;

    /** Exit code. */
    private int _exitCode = -1;

   /** Used to return a move entered from the console.  Allocated
     *  here to avoid allocations. */
    private final int[] _move = new int[2];
}
