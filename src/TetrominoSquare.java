public class TetrominoSquare {
    public enum Colours {
        CYAN,
        BLUE,
        ORANGE,
        YELLOW,
        GREEN,
        PURPLE,
        RED,
        EMPTY
    }

    public enum State {
        EMPTY,
        PLACED,
        FALLING
    }

    public Colours colour;
    public State state;

    public TetrominoSquare(Colours colour, State state) {
        this.colour = colour;
        this.state = state;
    }
}
