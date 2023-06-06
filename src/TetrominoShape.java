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
                    {1, 1},
                    {1, 1}
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

    private boolean isEmptyRow(TetrominoSquare[] row) {
        for (TetrominoSquare square : row) {
            if (square.state != TetrominoSquare.State.EMPTY) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmptyColumn(TetrominoSquare[][] squares, int column) {
        for (TetrominoSquare[] row : squares) {
            if (row[column].state != TetrominoSquare.State.EMPTY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes all empty rows and columns from the tetromino square array
     *
     * @return a copy of the tetromino square array with all empty rows and columns removed
     */
    public TetrominoSquare[][] shrink() {
        int startRow = 0;
        int endRow = this.squares.length - 1;

        // Find the first non-empty row
        for (int i = 0; i < this.squares.length; i++) {
            if (isEmptyRow(this.squares[i])) {
                startRow = i;
                break;
            }
        }

        // Find the last non-empty row
        for (int i = this.squares.length - 1; i >= 0; i--) {
            if (isEmptyRow(this.squares[i])) {
                endRow = i;
                break;
            }
        }

        int startColumn = 0;
        int endColumn = this.squares[0].length - 1;

        // Find the first non-empty column
        for (int i = 0; i < this.squares[0].length; i++) {
            if (isEmptyColumn(this.squares, i)) {
                startColumn = i;
                break;
            }
        }

        // Find the last non-empty column
        for (int i = this.squares[0].length - 1; i >= 0; i--) {
            if (isEmptyColumn(this.squares, i)) {
                endColumn = i;
                break;
            }
        }

        // Create a new array with the correct size
        TetrominoSquare[][] shrunkSquares = new TetrominoSquare[endRow - startRow + 1][endColumn - startColumn + 1];

        // Copy the squares from the original array to the new array
        for (int i = startRow; i <= endRow; i++) {
            if (endColumn + 1 - startColumn >= 0)
                System.arraycopy(this.squares[i], startColumn, shrunkSquares[i - startRow], 0, endColumn + 1 - startColumn);
        }
        return shrunkSquares;
    }

    public void printPiece() {
        for (TetrominoSquare[] row : this.squares) {
            for (TetrominoSquare square : row) {
                System.out.print(square.state == TetrominoSquare.State.EMPTY ? "O" : "X");
            }
            System.out.println();
        }
    }

    public void printShrunkenPiece() {
        TetrominoSquare[][] shrunkSquares = shrink();
        for (TetrominoSquare[] row : shrunkSquares) {
            for (TetrominoSquare square : row) {
                System.out.print(square.state == TetrominoSquare.State.EMPTY ? "O" : "X");
            }
            System.out.println();
        }
    }
}
