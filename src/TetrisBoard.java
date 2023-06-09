import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class TetrisBoard {
    // Possible directions to move the piece
    public enum MovementDirection {
        LEFT, RIGHT, DOWN
    }

    /**
     * Generates a random integer between min and max (inclusive)
     *
     * @param min The minimum number
     * @param max The maximum number
     * @return A random integer between min and max (inclusive)
     */
    private int randomInteger(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    // The List of the coordinates for the non-empty squares of the current piece
    // (0, 0) is the top left of the board
    // (9, 23) is the bottom right of the board
    private ArrayList<Point> currentPieceCoordinates;

    // 5 tetrominoes in the queue
    private Queue<TetrominoShape> tetrominoQueue;

    /**
     * Returns the current tetromino queue
     *
     * @return The current tetromino queue
     */
    public Queue<TetrominoShape> tetrominoQueue() {
        return this.tetrominoQueue;
    }

    // The current piece
    private TetrominoShape currentPiece;

    // The top left corner of the bounding box for the current piece
    private Point currentPieceTopLeftCorner;
    private ArrayList<Point> shadowCoordinates;

    // Stores the highest row that contains a piece, 0 is the h ighest row
    private int highestPieceRow;

    // Wall kicks following this guideline https://harddrop.com/wiki/SRS
    private enum WallKickRotations {
        // Spawn -> one 90-degree clockwise rotation
        ZERO_ONE,
        // One 90-degree clockwise rotation -> spawn
        ONE_ZERO,
        // One 90-degree clockwise rotation -> 180-degree rotation from spawn
        ONE_TWO,
        // 180-degree rotation from spawn -> one 90-degree clockwise rotation
        TWO_ONE,
        // 180-degree rotation from spawn -> 90-degree counter-clockwise rotation from spawn
        TWO_THREE,
        // 90-degree counter-clockwise rotation from spawn -> 180-degree rotation from spawn
        THREE_TWO,
        // 90-degree counter-clockwise rotation from spawn -> spawn
        THREE_ZERO,
        // Spawn -> 90-degree counter-clockwise rotation from spawn
        ZERO_THREE
    }

    // Positive X = translate right, Positive Y = translate down 
    private static final Map<WallKickRotations, Point[]> NON_I_WALL_KICK_DATA = new HashMap<>() {{
        put(WallKickRotations.ZERO_ONE, new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(-1, -1),
                new Point(0, 2),
                new Point(-1, 2)
        });
        put(WallKickRotations.ONE_ZERO, new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, 1),
                new Point(0, -2),
                new Point(1, -2)
        });
        put(WallKickRotations.ONE_TWO, new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, 1),
                new Point(0, -2),
                new Point(1, -2)
        });
        put(WallKickRotations.TWO_ONE, new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(-1, -1),
                new Point(0, 2),
                new Point(-1, 2)
        });
        put(WallKickRotations.TWO_THREE, new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, -1),
                new Point(0, 2),
                new Point(1, 2)
        });
        put(WallKickRotations.THREE_TWO, new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(-1, 1),
                new Point(0, -2),
                new Point(-1, -2)
        });
        put(WallKickRotations.THREE_ZERO, new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(-1, 1),
                new Point(0, -2),
                new Point(-1, -2)
        });
        put(WallKickRotations.ZERO_THREE, new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(1, -1),
                new Point(0, 2),
                new Point(1, 2)
        });
    }};

    public static final Map<WallKickRotations, Point[]> I_WALL_KICK_DATA = new HashMap<>() {{
        put(WallKickRotations.ZERO_ONE, new Point[]{
                new Point(0, 0),
                new Point(-2, 0),
                new Point(1, 0),
                new Point(-2, 1),
                new Point(1, -2)
        });
        put(WallKickRotations.ONE_ZERO, new Point[]{
                new Point(0, 0),
                new Point(2, 0),
                new Point(-1, 0),
                new Point(2, -1),
                new Point(-1, 2)
        });
        put(WallKickRotations.ONE_TWO, new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(2, 0),
                new Point(-1, -2),
                new Point(2, 1)
        });
        put(WallKickRotations.TWO_ONE, new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(-2, 0),
                new Point(1, 2),
                new Point(-2, -1)
        });
        put(WallKickRotations.TWO_THREE, new Point[]{
                new Point(0, 0),
                new Point(2, 0),
                new Point(-1, 0),
                new Point(2, -1),
                new Point(-1, 2)
        });
        put(WallKickRotations.THREE_TWO, new Point[]{
                new Point(0, 0),
                new Point(-2, 0),
                new Point(1, 0),
                new Point(-2, 1),
                new Point(1, -2)
        });
        put(WallKickRotations.THREE_ZERO, new Point[]{
                new Point(0, 0),
                new Point(1, 0),
                new Point(-2, 0),
                new Point(1, 2),
                new Point(-2, -1)
        });
        put(WallKickRotations.ZERO_THREE, new Point[]{
                new Point(0, 0),
                new Point(-1, 0),
                new Point(2, 0),
                new Point(-1, -2),
                new Point(2, 1)
        });

    }};

    /**
     * Returns a tetromino with a random shape
     *
     * @return a tetromino with a random shape
     */
    public TetrominoShape randomTetrominoShape() {
        return new TetrominoShape(TetrominoShape.Shapes.values()[randomInteger(0, 6)]);
    }

    // 20 * 10 board
    // The extra 4 rows are for the tetrominoes to spawn
    public TetrominoSquare[][] board;

    // The player's score
    private int score;

    /**
     * Returns the player's score
     *
     * @return the player's score
     */
    public int score() {
        return this.score;
    }

    /**
     * Resets the player's score to 0
     */
    private void resetScore() {
        this.score = 0;
    }

    public TetrisBoard() {
        // Initialize the board and other variables
        this.board = new TetrominoSquare[10][24];
        this.tetrominoQueue = new LinkedList<>();
        this.currentPieceCoordinates = new ArrayList<>();
        this.shadowCoordinates = new ArrayList<>();
        this.highestPieceRow = 24;

        // Fill the board with empty squares
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 24; j++) {
                this.board[i][j] = new TetrominoSquare(TetrominoSquare.Colours.EMPTY, TetrominoSquare.State.EMPTY);
            }
        }

        // Fill the queue with 5 tetrominoes
        for (int i = 0; i < 5; i++) {
            this.tetrominoQueue.add(randomTetrominoShape());
        }
    }

    /**
     * Spawns a new tetromino at the center top of the board
     */
    public void spawnNewTetromino() {
        // Remove the current piece from the queue and add a new one
        this.currentPiece = this.tetrominoQueue.remove();
        this.tetrominoQueue.add(randomTetrominoShape());

        // Reset the coordinates of the current piece
        this.currentPieceCoordinates.clear();
        currentPieceTopLeftCorner = new Point(3, 2);

        // Merge the piece into the array
        for (int i = 0; i < this.currentPiece.squares[0].length; i++) {
            for (int j = 0; j < this.currentPiece.squares.length; j++) {
                if (this.currentPiece.squares[j][i].state != TetrominoSquare.State.EMPTY) {
                    this.board[i + 3][j + 2].colour = this.currentPiece.squares[j][i].colour;
                    this.board[i + 3][j + 2].state = this.currentPiece.squares[j][i].state;
                    this.currentPieceCoordinates.add(new Point(i + 3, j + 2));
                }
            }
        }

        shadowCoordinates = calculateFall();
        for (Point point : shadowCoordinates) {
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.SHADOW;
        }
    }

    /**
     * Places the current piece on the board
     */
    public void placePiece() {
        for (Point point : shadowCoordinates) {
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
        }

        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].state = TetrominoSquare.State.PLACED;
            this.highestPieceRow = Math.min(this.highestPieceRow, point.y);
        }
    }

    /**
     * Calculates where the current piece would fall if it were to fall straight down
     *
     * @return an array list that contains the coordinates of the current piece after the fall
     */
    public ArrayList<Point> calculateFall() {
        ArrayList<Point> newCoordinates = new ArrayList<>();
        int amountToFall = 0;
        for (Point point : this.currentPieceCoordinates) {
            for (int i = 0; i <= 23; i++) {
                if (this.board[point.x][i].state == TetrominoSquare.State.PLACED || i == 23) {
                    amountToFall = Math.max(amountToFall, i - point.y - 1);
                    break;
                }
            }
        }

        for (Point point : this.currentPieceCoordinates) {
            newCoordinates.add(
                    new Point(point.x,
                            point.y + amountToFall
                    )
            );
        }

        return newCoordinates;
    }

    /**
     * Attempts to move a piece in the specified direction
     *
     * @param direction the direction to move the piece in
     * @return an array list that contains the coordinates of the current piece after the attempted move,
     * it will be empty if it cannot move.
     */
    public ArrayList<Point> tryMovePiece(MovementDirection direction) {
        // Create a translation vector
        // X+ = right, Y+ = down
        Point translation = new Point(0, 0);
        ArrayList<Point> newCoordinates = new ArrayList<>();
        if (direction == MovementDirection.LEFT) {
            translation.x = -1;
        } else if (direction == MovementDirection.RIGHT) {
            translation.x = 1;
        } else if (direction == MovementDirection.DOWN) {
            translation.y = 1;
        }

        // Check if the new position of each point is inside the board or if it is obstructed
        for (Point point : this.currentPieceCoordinates) {
            if (point.y + translation.y >= 24 || point.y + translation.y < 0 ||
                    point.x + translation.x >= 10 || point.x + translation.x < 0) {
                newCoordinates.clear();
                return newCoordinates;
            }
            if (board[point.x + translation.x][point.y + translation.y].state == TetrominoSquare.State.PLACED) {
                newCoordinates.clear();
                return newCoordinates;
            }
            newCoordinates.add(new Point(point.x + translation.x, point.y + translation.y));
        }

        return newCoordinates;
    }

    public void movePiece(MovementDirection direction) {
        ArrayList<Point> newCoordinates = tryMovePiece(direction);
        if (newCoordinates.isEmpty()) return;

        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
        }

        this.currentPieceCoordinates = newCoordinates;
        if (direction == MovementDirection.DOWN) {
            this.currentPieceTopLeftCorner.y++;
        } else if (direction == MovementDirection.LEFT) {
            this.currentPieceTopLeftCorner.x--;
        } else if (direction == MovementDirection.RIGHT) {
            this.currentPieceTopLeftCorner.x++;
        }

        for (Point point : shadowCoordinates) {
            if (this.board[point.x][point.y].colour == TetrominoSquare.Colours.SHADOW) {
                this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
            }
        }

        shadowCoordinates = calculateFall();
        for (Point point : shadowCoordinates) {
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.SHADOW;
        }

        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].colour = this.currentPiece.colour;
            this.board[point.x][point.y].state = TetrominoSquare.State.FALLING;
        }

        // TODO: Make it so it doesn't instant drop, maybe in a different method
        if (tryMovePiece(MovementDirection.DOWN).isEmpty()) {
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
        // Count how many rows need to be moved down
        // Not the number of rows that will be moved, but the number of times they will be moved
        int numberOfRowsToMoveDown = 0;
        for (int i = bottom; i >= top; i--) {
            if (removedRows.contains(i)) {
                numberOfRowsToMoveDown++;
                continue;
            }

            // Shift the row down
            for (int j = 0; j < 10; j++) {
                this.board[j][i + numberOfRowsToMoveDown].colour = this.board[j][i].colour;
                this.board[j][i + numberOfRowsToMoveDown].state = this.board[j][i].state;
                this.board[j][i].colour = TetrominoSquare.Colours.EMPTY;
                this.board[j][i].state = TetrominoSquare.State.EMPTY;
            }
        }

        this.highestPieceRow += numberOfRowsToMoveDown;
    }

    // TODO: Will remove this in the final game
    public void printBoard() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 10; j++) {
                if (this.board[j][i].colour == TetrominoSquare.Colours.SHADOW) {
                    System.out.print("\u001B[33m" + this.board[j][i].colour.ordinal() + "\u001B[0m");
                } else if (i == currentPieceTopLeftCorner.y && j == currentPieceTopLeftCorner.x) {
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
