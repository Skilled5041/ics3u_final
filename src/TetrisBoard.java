import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class TetrisBoard {
    public enum MovementDirection {
        LEFT, RIGHT, DOWN
    }

    private int randomInteger(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    private ArrayList<Point> currentPieceCoordinates;
    private ArrayList<Point> currentPieceRelativeCoordinates;

    // 5 tetrominoes in the queue
    private Queue<TetrominoShape> tetrominoQueue;

    private Queue<TetrominoShape> tetrominoQueue() {
        return this.tetrominoQueue;
    }

    private TetrominoShape currentPiece;
    private Point currentPieceTopLeftCorner;

    // Stores the highest row that contains a piece, 0 is the highest row
    private int highestPieceRow;

    public TetrominoShape randomTetrominoShape() {
        return new TetrominoShape(TetrominoShape.Shapes.values()[randomInteger(0, 6)]);
    }

    // 20 * 10 board
    // The extra 4 rows are for the tetrominoes to spawn
    public TetrominoSquare[][] board;
    private int score;

    public int score() {
        return this.score;
    }

    private void resetScore() {
        this.score = 0;
    }

    public TetrisBoard() {
        this.board = new TetrominoSquare[10][24];
        this.tetrominoQueue = new LinkedList<>();
        this.currentPieceCoordinates = new ArrayList<>();
        this.currentPieceRelativeCoordinates = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 24; j++) {
                this.board[i][j] = new TetrominoSquare(TetrominoSquare.Colours.EMPTY, TetrominoSquare.State.EMPTY);
            }
        }

        for (int i = 0; i < 5; i++) {
            this.tetrominoQueue.add(randomTetrominoShape());
        }
    }

    public void spawnNewTetromino() {
        this.currentPiece = this.tetrominoQueue.remove();
        this.tetrominoQueue.add(randomTetrominoShape());
        this.currentPieceCoordinates.clear();
        this.currentPieceRelativeCoordinates.clear();
        this.highestPieceRow = 24;

        int xOffset = this.currentPiece.squares[0].length % 2;
        int yOffset = 0;
        if (this.currentPiece.shape == TetrominoShape.Shapes.I || this.currentPiece.shape == TetrominoShape.Shapes.O) {
            xOffset = 1;
        }

        int halfLength = this.currentPiece.squares.length / 2;

        currentPieceTopLeftCorner = new Point(4 - xOffset, 2 + yOffset);

        // Merge the piece into the array
        for (int i = 0; i < this.currentPiece.squares[0].length; i++) {
            for (int j = 0; j < this.currentPiece.squares.length; j++) {
                if (this.currentPiece.squares[j][i].state != TetrominoSquare.State.EMPTY) {
                    this.board[i + 4 - xOffset][j + 2 + yOffset].colour = this.currentPiece.squares[j][i].colour;
                    this.board[i + 4 - xOffset][j + 2 + yOffset].state = this.currentPiece.squares[j][i].state;
                    this.currentPieceCoordinates.add(new Point(i + 4 - xOffset, j + 2 + yOffset));

                    int relativeX = i - halfLength;
                    int relativeY = j - halfLength;

                    // Edge case fixes for even length pieces
                    if (this.currentPiece.squares.length % 2 == 0) {
                        if (this.currentPiece.shape == TetrominoShape.Shapes.I) {
                            relativeY = -1;
                        } else if (j - halfLength >= 0) {
                            relativeY++;
                        }
                        if (i - halfLength >= 0) {
                            relativeX++;
                        }
                    }
                    this.currentPieceRelativeCoordinates.add(new Point(relativeX, relativeY));
                }
            }
        }
    }

    public void spawnNewTetromino(TetrominoShape.Shapes shapes) {
        this.currentPiece = new TetrominoShape(shapes);
        this.currentPieceCoordinates.clear();
        this.currentPieceRelativeCoordinates.clear();
        this.highestPieceRow = 24;

        int xOffset = this.currentPiece.squares[0].length % 2;
        int yOffset = 0;
        if (this.currentPiece.shape == TetrominoShape.Shapes.I) {
            xOffset = 1;
            yOffset = 1;
        }

        int halfLength = this.currentPiece.squares.length / 2;

        // Merge the piece into the array
        for (int i = 0; i < this.currentPiece.squares[0].length; i++) {
            for (int j = 0; j < this.currentPiece.squares.length; j++) {
                if (this.currentPiece.squares[j][i].state != TetrominoSquare.State.EMPTY) {
                    this.board[i + 4 - xOffset][j + 2 + yOffset].colour = this.currentPiece.squares[j][i].colour;
                    this.board[i + 4 - xOffset][j + 2 + yOffset].state = this.currentPiece.squares[j][i].state;
                    this.currentPieceCoordinates.add(new Point(i + 4 - xOffset, j + 2 + yOffset));

                    int relativeX = i - halfLength;
                    int relativeY = j - halfLength;

                    // Edge case fixes for even length pieces
                    if (this.currentPiece.squares.length % 2 == 0) {
                        if (this.currentPiece.shape == TetrominoShape.Shapes.I) {
                            relativeY = -1;
                        } else if (j - halfLength >= 0) {
                            relativeY++;
                        }
                        if (i - halfLength >= 0) {
                            relativeX++;
                        }
                    }
                    this.currentPieceRelativeCoordinates.add(new Point(relativeX, relativeY));
                }
            }
        }
    }

    public void placePiece() {
        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].state = TetrominoSquare.State.PLACED;
            this.highestPieceRow = Math.min(this.highestPieceRow, point.y);
        }
    }

    public boolean canMovePiece(MovementDirection direction) {
        if (direction == MovementDirection.DOWN) {
            for (Point point : this.currentPieceCoordinates) {
                if (point.y + 1 >= 24) {
                    return false;
                }
                if (board[point.x][point.y + 1].state == TetrominoSquare.State.PLACED) {
                    return false;
                }
            }
            return true;

        } else if (direction == MovementDirection.LEFT) {
            for (Point point : this.currentPieceCoordinates) {
                if (point.x - 1 < 0) {
                    return false;
                }
                if (board[point.x - 1][point.y].state == TetrominoSquare.State.PLACED) {
                    return false;
                }
            }
            return true;

        } else if (direction == MovementDirection.RIGHT) {
            for (Point point : this.currentPieceCoordinates) {
                if (point.x + 1 >= 10) {
                    return false;
                }
                if (board[point.x + 1][point.y].state == TetrominoSquare.State.PLACED) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public void movePiece(MovementDirection direction) {
        // TODO: Might change canMovePiece to somewhere else
        if (direction == MovementDirection.DOWN && canMovePiece(MovementDirection.DOWN)) {

            for (Point point : this.currentPieceCoordinates) {
                this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
                this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            }

            for (Point point : this.currentPieceCoordinates) {
                point.y++;
                this.board[point.x][point.y].colour = this.currentPiece.colour;
                this.board[point.x][point.y].state = TetrominoSquare.State.FALLING;
            }
            this.currentPieceTopLeftCorner.y++;
        } else if (direction == MovementDirection.LEFT && canMovePiece(MovementDirection.LEFT)) {

            for (Point point : this.currentPieceCoordinates) {
                this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
                this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            }

            for (Point point : this.currentPieceCoordinates) {
                this.board[--point.x][point.y].colour = this.currentPiece.colour;
                this.board[point.x][point.y].state = TetrominoSquare.State.FALLING;
            }
            this.currentPieceTopLeftCorner.x--;
        } else if (direction == MovementDirection.RIGHT && canMovePiece(MovementDirection.RIGHT)) {

            for (Point point : this.currentPieceCoordinates) {
                this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
                this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            }

            for (Point point : this.currentPieceCoordinates) {
                this.board[++point.x][point.y].colour = this.currentPiece.colour;
                this.board[point.x][point.y].state = TetrominoSquare.State.FALLING;
            }
            this.currentPieceTopLeftCorner.x++;
        }


        if (!canMovePiece(MovementDirection.DOWN)) {
            placePiece();
            ArrayList<Integer> rows = clearRows();
            int max = rows.stream().max(Integer::compare).isPresent() ? rows.stream().max(Integer::compare).get() : -1;
            if (max != -1) {
                moveRowsDown(max, highestPieceRow + 1, rows);
            }
            spawnNewTetromino();
        }
    }

    /**
     * Rotates a piece a specified number of times, if possible
     *
     * @param rotations the number of 90 degree rotations, 1 for clockwise, 3 for counter-clockwise
     */
    public void rotatePiece(int rotations) throws CloneNotSupportedException {
        rotations %= currentPiece.getNumberOfRotationStates();

        if (rotations == 0 || currentPiece.shape == TetrominoShape.Shapes.O) {
            return;
        }

        if (!canRotatePiece(rotations)) {
            return;
        }

        currentPiece.setRotation(currentPiece.getRotation() + rotations);

        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
            this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
        }

        this.currentPieceCoordinates.clear();

        for (int i = 0; i < currentPiece.squares.length; i++) {
            for (int j = 0; j < currentPiece.squares[i].length; j++) {
                if (currentPiece.squares[i][j].state == TetrominoSquare.State.FALLING) {
                    this.currentPieceCoordinates.add(new Point(currentPieceTopLeftCorner.x + i, currentPieceTopLeftCorner.y + j));
                    this.board[currentPieceTopLeftCorner.x + i][currentPieceTopLeftCorner.y + j].colour = currentPiece.colour;
                    this.board[currentPieceTopLeftCorner.x + i][currentPieceTopLeftCorner.y + j].state = TetrominoSquare.State.FALLING;
                }
            }
        }
    }

    /**
     * Checks to see if a piece can rotate
     *
     * @param rotations the number of 90 degree rotations.
     *                  1 for clockwise, 3 for counter-clockwise
     */
    public boolean canRotatePiece(int rotations) throws CloneNotSupportedException {
        rotations %= currentPiece.getNumberOfRotationStates();

        if (rotations == 0) {
            return true;
        }

        TetrominoShape rotated = currentPiece.clone();
        rotated.setRotation(rotated.getRotation() + rotations);

        for (int i = 0; i < rotated.squares.length; i++) {
            for (int j = 0; j < rotated.squares[i].length; j++) {
                // Check if index out of bounds
                if ((currentPieceTopLeftCorner.x + i < 0 || currentPieceTopLeftCorner.x + i >= 10 ||
                        currentPieceTopLeftCorner.y + j < 0 || currentPieceTopLeftCorner.y + j >= 24) && rotated.squares[i][j].state == TetrominoSquare.State.FALLING) {
                    return false;
                }
                if (rotated.squares[i][j].state == TetrominoSquare.State.FALLING) {
                    int x = currentPieceTopLeftCorner.x + i;
                    int y = currentPieceTopLeftCorner.y + j;
                    if (x < 0 || x >= 10 || y < 0 || y >= 24 || board[x][y].state == TetrominoSquare.State.PLACED) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Clears all rows that are full
     *
     * @return A list of rows that were cleared
     */
    public ArrayList<Integer> clearRows() {
        // Store all rows that might be full
        ArrayList<Integer> rowsToCheck = new ArrayList<>();
        for (Point point : this.currentPieceCoordinates) {
            if (!rowsToCheck.contains(point.y)) {
                rowsToCheck.add(point.y);
            }
        }

        ArrayList<Integer> removedRows = new ArrayList<>();

        // Check if the rows are full
        for (int row : rowsToCheck) {
            boolean rowIsFull = true;
            for (int i = 0; i < 10; i++) {
                if (this.board[i][row].state != TetrominoSquare.State.PLACED) {
                    rowIsFull = false;
                    break;
                }
            }
            // Clear the row if it is full
            if (rowIsFull) {
                removedRows.add(row);
                for (int i = 0; i < 10; i++) {
                    this.board[i][row].state = TetrominoSquare.State.EMPTY;
                    this.board[i][row].colour = TetrominoSquare.Colours.EMPTY;
                }
            }
        }
        return removedRows;
    }


    /**
     * Shifts down each column down as much as possible. Top should be < bottom
     *
     * @param top         the topmost row that should be shifted
     * @param bottom      the bottommost row that should be shifted
     * @param removedRows the rows that were removed (they will be empty)
     */
    private void moveRowsDown(int bottom, int top, ArrayList<Integer> removedRows) {
        int numberOfRowsToMoveDown = 0;
        for (int i = bottom; i >= top; i--) {
            if (removedRows.contains(i)) {
                numberOfRowsToMoveDown++;
                continue;
            }

            for (int j = 0; j < 10; j++) {
                this.board[j][i + numberOfRowsToMoveDown].colour = this.board[j][i].colour;
                this.board[j][i + numberOfRowsToMoveDown].state = this.board[j][i].state;
                this.board[j][i].colour = TetrominoSquare.Colours.EMPTY;
                this.board[j][i].state = TetrominoSquare.State.EMPTY;
            }
        }

        this.highestPieceRow += numberOfRowsToMoveDown;
    }

    public void printBoard() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == currentPieceTopLeftCorner.y && j == currentPieceTopLeftCorner.x) {
                    System.out.print("\u001B[35m" + this.board[j][i].colour.ordinal() + "\u001B[0m");
                } else if (this.board[j][i].state == TetrominoSquare.State.EMPTY) {
                    System.out.print("\u001B[31m" + this.board[j][i].colour.ordinal() + "\u001B[0m");
                } else if (this.board[j][i].state == TetrominoSquare.State.FALLING) {
                    System.out.print("\u001B[32m" + this.board[j][i].colour.ordinal() + "\u001B[0m");
                } else {
                    System.out.print("\u001B[34m" + this.board[j][i].colour.ordinal() + "\u001B[0m");
                }
            }
            System.out.println();
        }
    }
}
