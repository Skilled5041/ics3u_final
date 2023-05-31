import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class TetrisBoard {
    public enum MovementDirection {
        LEFT,
        RIGHT,
        DOWN
    }

    private int randomInteger(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    private ArrayList<Point> currentPieceCordinates;

    // 5 tetrominoes in the queue
    private Queue<TetrominoShape> tetrominoQueue;

    private Queue<TetrominoShape> tetrominoQueue() {
        return this.tetrominoQueue;
    }

    private TetrominoShape currentPiece;

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
        this.currentPieceCordinates = new ArrayList<>();

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
        this.currentPieceCordinates.clear();
        this.highestPieceRow = 24;

        TetrominoSquare[][] shrunkenShape = this.currentPiece.shrink();

        int xOffset = shrunkenShape[0].length % 2;
        int yOffset = 0;
        if (this.currentPiece.shape == TetrominoShape.Shapes.I) {
            xOffset = 1;
            yOffset = 1;
        }

        // Merge the piece into the array
        for (int i = 0; i < shrunkenShape[0].length; i++) {
            for (int j = 0; j < shrunkenShape.length; j++) {
                if (shrunkenShape[j][i].state != TetrominoSquare.State.EMPTY) {
                    this.board[i + 4 - xOffset][j + 2 + yOffset].colour = shrunkenShape[j][i].colour;
                    this.board[i + 4 - xOffset][j + 2 + yOffset].state = shrunkenShape[j][i].state;
                    this.currentPieceCordinates.add(new Point(i + 4 - xOffset, j + 2 + yOffset));
                }
            }
        }
    }

    public void placePiece() {
        for (Point point : this.currentPieceCordinates) {
            this.board[point.x][point.y].state = TetrominoSquare.State.PLACED;
            this.highestPieceRow = Math.min(this.highestPieceRow, point.y);
        }
    }

    public boolean canMovePiece(MovementDirection direction) {
        if (direction == MovementDirection.DOWN) {
            for (Point point : this.currentPieceCordinates) {
                if (point.y + 1 >= 24) {
                    return false;
                }
                if (board[point.x][point.y + 1].state == TetrominoSquare.State.PLACED) {
                    return false;
                }
            }
            return true;

        } else if (direction == MovementDirection.LEFT) {
            for (Point point : this.currentPieceCordinates) {
                if (point.x - 1 < 0) {
                    return false;
                }
                if (board[point.x - 1][point.y].state == TetrominoSquare.State.PLACED) {
                    return false;
                }
            }
            return true;

        } else if (direction == MovementDirection.RIGHT) {
            for (Point point : this.currentPieceCordinates) {
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

            for (Point point : this.currentPieceCordinates) {
                this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
                this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            }

            for (Point point : this.currentPieceCordinates) {
                point.y++;
                this.board[point.x][point.y].colour = this.currentPiece.colour;
                this.board[point.x][point.y].state = TetrominoSquare.State.FALLING;
            }
        } else if (direction == MovementDirection.LEFT && canMovePiece(MovementDirection.LEFT)) {

            for (Point point : this.currentPieceCordinates) {
                this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
                this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            }

            for (Point point : this.currentPieceCordinates) {
                this.board[--point.x][point.y].colour = this.currentPiece.colour;
                this.board[point.x][point.y].state = TetrominoSquare.State.FALLING;
            }
        } else if (direction == MovementDirection.RIGHT && canMovePiece(MovementDirection.RIGHT)) {

            for (Point point : this.currentPieceCordinates) {
                this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
                this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            }

            for (Point point : this.currentPieceCordinates) {
                this.board[++point.x][point.y].colour = this.currentPiece.colour;
                this.board[point.x][point.y].state = TetrominoSquare.State.FALLING;
            }
        }


        if (!canMovePiece(MovementDirection.DOWN)) {
            placePiece();
            ArrayList<Integer> rows = clearLines();
            System.out.println(rows);
            int max = rows.stream().max(Integer::compare).isPresent() ? rows.stream().max(Integer::compare).get() : -1;
            if (max != -1) {
                moveRowsDown(max, highestPieceRow);
            }
            spawnNewTetromino();
        }
    }

    /**
     * Clears all rows that are full
     *
     * @return A list of rows that were cleared
     */
    public ArrayList<Integer> clearLines() {
        // Store all rows that might be full
        ArrayList<Integer> rowsToCheck = new ArrayList<>();
        for (Point point : this.currentPieceCordinates) {
            if (!rowsToCheck.contains(point.y)) {
                rowsToCheck.add(point.y);
            }
        }

        // Check if the rows are full
        for (int row : rowsToCheck) {
            System.out.println("Checking row " + row);
            boolean rowIsFull = true;
            for (int i = 0; i < 10; i++) {
                if (this.board[i][row].state != TetrominoSquare.State.PLACED) {
                    rowIsFull = false;
                    rowsToCheck.remove((Integer) row);
                    break;
                }
            }
            // Clear the row if it is full
            if (rowIsFull) {
                for (int i = 0; i < 10; i++) {
                    this.board[i][row].state = TetrominoSquare.State.EMPTY;
                    this.board[i][row].colour = TetrominoSquare.Colours.EMPTY;
                }
            }
        }
        return rowsToCheck;
    }


    /**
     * Shifts down each column down as much as possible. top should be < bottom
     *
     * @param top    the topmost row that should be shifted
     * @param bottom the bottommost row that should be shifted
     */
    private void moveRowsDown(int bottom, int top) {
        // for each column calculate the number of rows that it must shift down by, and then shift it down
        System.out.println("Top: " + top + " Bottom: " + bottom);
        for (int i = 0; i < 10; i++) {
            int shiftAmount = 0;
            for (int j = bottom; j >= 0; j--) {
                if (this.board[i][j].state == TetrominoSquare.State.PLACED) {
                    break;
                } else {
                    shiftAmount++;
                }
            }
            for (int j = top; j >= bottom; j--) {
                if (this.board[i][j].state == TetrominoSquare.State.PLACED) {
                    this.board[i][j].state = TetrominoSquare.State.EMPTY;
                    this.board[i][j].colour = TetrominoSquare.Colours.EMPTY;
                    this.board[i][j - shiftAmount].state = TetrominoSquare.State.PLACED;
                    this.board[i][j - shiftAmount].colour = this.board[i][j].colour;
                }
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 10; j++) {
                if (this.board[j][i].colour == TetrominoSquare.Colours.EMPTY) {
                    System.out.print("\u001B[31m" + this.board[j][i].colour.ordinal() + "\u001B[0m");
                } else {
                    System.out.print("\u001B[32m" + this.board[j][i].colour.ordinal() + "\u001B[0m");
                }
            }
            System.out.println();
        }
    }
}
