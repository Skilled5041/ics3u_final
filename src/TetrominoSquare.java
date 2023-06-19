// Aaron Ye
// 2023-06-18
// Tetromino Square
// Represents a square in a tetromino / tetris board
public class TetrominoSquare {
    // Possible colours
    public enum Colours {
        CYAN,
        BLUE,
        ORANGE,
        YELLOW,
        GREEN,
        PURPLE,
        RED,
        // No pieces occupy the square
        EMPTY,
        // Where the current piece would drop to
        SHADOW,
        DISABLED
    }

    // Possible states
    public enum State {
        // Completely empty
        EMPTY,
        // Contains a square that is already placed
        PLACED,
        // Unplaced square / still falling
        FALLING
    }

    public Colours colour;
    public State state;

    public TetrominoSquare(Colours colour, State state) {
        this.colour = colour;
        this.state = state;
    }
}
