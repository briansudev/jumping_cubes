package jump61;

import static jump61.Color.*;

/** Represents a square in the board.
 *  @author Brian Su
 */
public class Square {

    /** Number of spots in the square. */
    private int _spots;
    /** Color of the square. */
    private Color _color;

    /** Create an empty white square. */
    public Square() {
        _color = WHITE;
        _spots = 0;
    }

    /** Create a square with COLOR and NUM of spots. */
    public Square(Color color, int num) {
        _color = color;
        _spots = num;
    }

    /** Returns the color of the square. */
    public Color getColor() {
        return _color;
    }

    /** Returns the number of spots the square has. */
    public int getSpots() {
        return _spots;
    }

    /** Sets the number of spots the square has to N. */
    public void setSpots(int n) {
        _spots = n;
    }

    /** Sets the color of the spot to COLOR. */
    public void setColor(Color color) {
        _color = color;
    }

    /** Returns the representation of a square. */
    public String toString() {
        if (_spots == 0) {
            return "--";
        } else {
            String c = _color.toString().substring(0, 1);
            return String.valueOf(_spots) + c;
        }
    }
}
