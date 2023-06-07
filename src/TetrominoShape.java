import java.awt.*;

public class TetrominoShape {
    public enum Shapes {
        I, J, L, O, S, T, Z
    }

    public int[][][] tetrominoShapeArrays = {
            // I
            {
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0}
            },
            // J
            {
                    {1, 0, 0},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            // L
            {
                    {0, 0, 1},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            // O
            {
                    {0, 1, 1, 0},
                    {0, 1, 1, 0},
                    {0, 0, 0, 0}
            },
            // S
            {
                    {0, 1, 1},
                    {1, 1, 0},
                    {0, 0, 0}
            },
            // T
            {
                    {0, 1, 0},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            // Z
            {
                    {1, 1, 0},
                    {0, 1, 1},
                    {0, 0, 0}}
    };

    public Shapes shape;
    public TetrominoSquare[][] squares;
    public TetrominoSquare.Colours colour;

    public TetrominoShape(Shapes shape) {
        this.shape = shape;
        this.colour = TetrominoSquare.Colours.values()[shape.ordinal()];

        // Initialise the array size to the size of the shape in tetrominoShapeArrays
        this.squares = new TetrominoSquare[tetrominoShapeArrays[shape.ordinal()].length]
                [tetrominoShapeArrays[shape.ordinal()][0].length];

        for (int i = 0; i < tetrominoShapeArrays[shape.ordinal()].length; i++) {
            for (int j = 0; j < tetrominoShapeArrays[shape.ordinal()][i].length; j++) {
                if (tetrominoShapeArrays[shape.ordinal()][i][j] == 1) {
                    this.squares[i][j] = new TetrominoSquare(TetrominoSquare.Colours.values()[shape.ordinal()],
                            TetrominoSquare.State.FALLING);
                } else {
                    this.squares[i][j] = new TetrominoSquare(TetrominoSquare.Colours.EMPTY,
                            TetrominoSquare.State.EMPTY);
                }
            }
        }
    }
}
