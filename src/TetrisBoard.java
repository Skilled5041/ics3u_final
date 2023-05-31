import java.awt.*;
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
                this.board[point.x][++point.y].colour = this.currentPiece.colour;
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
        }
    }

    public void clearBoard() {
        ArrayList<Integer> rowsToClear = new ArrayList<>();
        for (Point point : this.currentPieceCordinates) {
            if (!rowsToClear.contains(point.y)) {
                rowsToClear.add(point.y);
            }
        }

        for (int row : rowsToClear) {
            boolean rowIsFull = true;
            for (int i = 0; i < 10; i++) {
                if (this.board[i][row].state != TetrominoSquare.State.PLACED) {
                    rowIsFull = false;
                }
            }
            if (rowIsFull) {
                for (int i = 0; i < 10; i++) {
                    this.board[i][row].state = TetrominoSquare.State.EMPTY;
                    this.board[i][row].colour = TetrominoSquare.Colours.EMPTY;
                }
                for (int i = row; i > 0; i--) {
                    for (int j = 0; j < 10; j++) {
                        this.board[j][i].state = this.board[j][i - 1].state;
                        this.board[j][i].colour = this.board[j][i - 1].colour;
                    }
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
