// Aaron Ye
// 2023-06-18
// Tetris board
// Handles the game logic

import javax.swing.Timer;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class TetrisBoard { // Automatic repeat rate
    // How often the piece moves when the player holds down a key
    public static int arr = 0;
    // Delayed auto shift
    // How long the player has to hold down a key before the piece starts moving
    public static int das = 133;
    // Soft drop factor
    // How much faster the piece moves when the player holds down the down key
    // In ms
    public static int sdf = 0;
    public long lastSdfTime = -1;
    public boolean softDropping = false;
    // When the player started holding down a key
    // Stored as UNIX time
    // -1 if the player is not holding down a key
    public long dasTime = -1;
    public long lastDasTime = -1;

    // Possible directions to move the piece
    public enum MovementDirection {
        LEFT, RIGHT, DOWN
    }

    // The List of the coordinates for the non-empty squares of the current piece
    // (0, 0) is the top left of the board
    // (9, 23) is the bottom right of the board
    private ArrayList<Point> currentPieceCoordinates;

    // 5 tetrominoes in the queue
    private final Queue<TetrominoShape> tetrominoQueue;

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
    public TetrominoShape holdPiece = null;
    public boolean alreadyHeld = false;

    // Stores the highest row that contains a piece, 0 is the highest row
    public int highestPieceRow;

    private final Queue<TetrominoShape.Shapes> bag1 = new LinkedList<>();
    private final Queue<TetrominoShape.Shapes> bag2 = new LinkedList<>();

    // Number of rows per frame
    private double gravity = 0.02;
    public int linesCleared = 0;

    // Delay in ms
    public int autoFallDelay = (int) (Game.FRAME_TIME / gravity);

    // Piece locks after 0.5 seconds of no movement and being on the ground
    private int dropLockTime = 0;
    public boolean gameStarted = false;
    public boolean gameEnded = false;

    // Can reset the lock delay up to 15 times
    private int dropLockResetCount = 0;
    private int combo = 0;
    // How long it takes for the piece to lock after it drops
    private final int DROP_LOCK_DELAY = (int) (1000.0 / 60 * (30));
    public boolean toppedOut = false;

    /**
     * Stops a timers and sets the game to ended
     */
    public void endGame() {
        this.gameEnded = true;
        this.autoFallTimer.stop();
    }

    // Makes the piece automatically fall
    Timer autoFallTimer = new Timer(autoFallDelay, e -> movePiece(MovementDirection.DOWN));

    /**
     * Runs every frame (1000 / 60 ms)
     */
    public void gameFrame() {
        // Make the piece fall automatically
        if (!gameStarted || gameEnded) {
            autoFallTimer.stop();
            return;
        } else if (!autoFallTimer.isRunning()) {
            autoFallTimer.start();
        }

        // Update the gravity
        gravity += 0.00001;
        autoFallDelay = (int) (Game.FRAME_TIME / gravity);
        autoFallTimer.setDelay(autoFallDelay);

        if (tryMovePiece(MovementDirection.DOWN).isEmpty()) {
            // Reset the drop lock reset time
            dropLockTime += 1000 / 60;
        }

        // Lock the piece if it has been on the ground for a while
        if (dropLockTime >= DROP_LOCK_DELAY || dropLockResetCount >= 15) {
            dropLockTime = 0;
            dropLockResetCount = 0;
            TetrisGraphics.playSound("soft_drop");
            placePiece();
        }
    }

    /**
     * Gets a piece from the 7-bag implementation. Each cycle contains each piece once.
     *
     * @return a piece from the 7-bag implementation
     */
    public TetrominoShape getPieceFromBag() {
        // Get from the first bag
        TetrominoShape piece = new TetrominoShape(this.bag1.remove());
        this.bag1.add(this.bag2.remove());

        // Extra buffer to be able to show the next 5 pieces
        if (this.bag2.isEmpty()) {
            TetrominoShape.Shapes[] shapes = TetrominoShape.Shapes.values();
            Collections.shuffle(Arrays.asList(shapes));
            this.bag2.addAll(Arrays.asList(shapes));
        }

        return piece;
    }

    /**
     * Holds the current piece if it has not been held yet
     */
    public void hold() throws CloneNotSupportedException {
        if (alreadyHeld) return;

        TetrisGraphics.playSound("hold");
        // Clear the current piece
        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
        }

        // Clear the shadow
        for (Point point : this.shadowCoordinates) {
            this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
        }

        // Hold the piece
        if (this.holdPiece == null) {
            this.holdPiece = this.currentPiece.clone();
            holdPiece.setRotation(0);
            spawnNewTetromino();
        } else {
            TetrominoShape temp = this.currentPiece.clone();
            this.currentPiece = this.holdPiece.clone();
            this.holdPiece = temp;
            this.holdPiece.setRotation(0);

            spawnNewTetromino(currentPiece);
        }

        alreadyHeld = true;
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

        TetrominoShape.Shapes[] randomBag1 = TetrominoShape.Shapes.values();
        Collections.shuffle(Arrays.asList(randomBag1));

        TetrominoShape.Shapes[] randomBag2 = TetrominoShape.Shapes.values();
        Collections.shuffle(Arrays.asList(randomBag2));

        this.bag1.addAll(Arrays.asList(randomBag2).subList(0, 7));
        this.bag2.addAll(Arrays.asList(randomBag1).subList(0, 7));

        // Fill the queue with 5 tetrominoes
        for (int i = 0; i < 5; i++) {
            this.tetrominoQueue.add(getPieceFromBag());
        }

        autoFallTimer.start();
    }

    /**
     * Spawns a new tetromino at the center top of the board, from the queue
     */
    public void spawnNewTetromino() {
        this.tetrominoQueue.add(getPieceFromBag());
        spawnNewTetromino(this.tetrominoQueue.remove());
    }

    /**
     * Spawns a new tetromino at the center top of the board
     *
     * @param piece the shape of the tetromino to spawn
     */
    public void spawnNewTetromino(TetrominoShape piece) {
        // Remove the current piece from the queue and add a new one
        this.currentPiece = piece;
        this.alreadyHeld = false;

        // Reset the coordinates of the current piece
        this.currentPieceCoordinates.clear();
        currentPieceTopLeftCorner = new Point(3, 2);

        // Check if player is dead
        for (int i = 0; i < this.currentPiece.squares[0].length; i++) {
            for (int j = 0; j < this.currentPiece.squares.length; j++) {
                if (this.currentPiece.squares[j][i].state != TetrominoSquare.State.EMPTY) {
                    if (this.board[i + 3][j + 2].state != TetrominoSquare.State.EMPTY) {
                        this.toppedOut = true;
                        return;
                    }
                }
            }
        }

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
        renderShadow(shadowCoordinates);
    }

    /**
     * Displays a shadow on the board
     *
     * @param shadowCoordinates the coordinates of the shadow
     */
    private void renderShadow(ArrayList<Point> shadowCoordinates) {
        if (shadowCoordinates == null) return;
        for (Point point : shadowCoordinates) {
            if (point.y < 4) continue;
            if (this.board[point.x][point.y].state != TetrominoSquare.State.EMPTY) continue;
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.SHADOW;
        }
    }

    enum ClearTypes {
        SINGLE, DOUBLE, TRIPLE, QUAD, PERFECT_CLEAR
    }

    // The score for each type of clear
    private final static Map<ClearTypes, Integer> SCORE_MAP = new HashMap<>() {{
        put(ClearTypes.SINGLE, 100);
        put(ClearTypes.DOUBLE, 300);
        put(ClearTypes.TRIPLE, 500);
        put(ClearTypes.QUAD, 800);
        put(ClearTypes.PERFECT_CLEAR, 3500);
    }};

    /**
     * Determines if the board is in a perfect clear state
     *
     * @return true if the board is in a perfect clear state, false otherwise
     */
    public boolean isPerfectClear() {
        for (int i = 0; i < 10; i++) {
            if (this.board[i][23].state != TetrominoSquare.State.EMPTY) return false;
        }
        return true;
    }

    /**
     * Places the current piece on the board
     */
    public void placePiece() {
        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].state = TetrominoSquare.State.PLACED;
            this.highestPieceRow = Math.min(this.highestPieceRow, point.y);
        }

        // Try clearing lines
        ArrayList<Integer> rows = clearLines();

        // Move the rows down
        linesCleared += rows.size();
        int max = rows.stream().max(Integer::compare).isPresent() ? rows.stream().max(Integer::compare).get() : -1;
        if (max != -1) {
            moveRowsDown(max, highestPieceRow, rows);
        }

        // Handle score
        if (rows.size() == 0) combo = 0;
        else {
            combo++;
            ClearTypes clearType = ClearTypes.values()[rows.size() - 1];
            score += SCORE_MAP.get(clearType);
            score += combo * 50;
            if (rows.size() <= 3) {
                TetrisGraphics.playSound("clear_line");
            } else {
                TetrisGraphics.playSound("clear_quad");
            }
            if (isPerfectClear()) {
                score += SCORE_MAP.get(ClearTypes.PERFECT_CLEAR);
                TetrisGraphics.playSound("perfect_clear");
            }
        }

        spawnNewTetromino();
    }

    /**
     * Calculates where the current piece would fall if it were to fall straight down
     *
     * @return an array list that contains the coordinates of the current piece after the fall
     */
    public ArrayList<Point> calculateFall() {
        // Only include the lowest (highest) Y coordinate of each column
        ArrayList<Point> filteredCoordinates = new ArrayList<>();
        ArrayList<Point> newCoordinates = new ArrayList<>();

        for (Point point : currentPieceCoordinates) {
            boolean add = true;
            for (Point filteredPoint : filteredCoordinates) {
                if (point.x == filteredPoint.x) {
                    add = false;
                    if (point.y > filteredPoint.y) {
                        filteredPoint.y = point.y;
                    }
                }
            }
            if (add) {
                filteredCoordinates.add(new Point(point.x, point.y));
            }
        }

        int amountToFall = 24;
        for (Point point : filteredCoordinates) {
            // Check how far down the piece can fall
            for (int i = point.y + 1; i <= 23; i++) {
                if (this.board[point.x][i].state == TetrominoSquare.State.PLACED) {
                    amountToFall = Math.min(amountToFall, i - point.y - 1);
                    break;
                } else if (i == 23) {
                    amountToFall = Math.min(amountToFall, i - point.y);
                }
            }
        }

        for (Point point : currentPieceCoordinates) {
            if (point.y + (amountToFall == 24 ? 0 : amountToFall) > 23) {
                return this.currentPieceCoordinates;
            }
            newCoordinates.add(new Point(point.x, point.y + (amountToFall == 24 ? 0 : amountToFall)));
        }

        return newCoordinates;
    }

    /**
     * Moves the current piece down as far as it can go, and drops it
     */
    public void hardDrop() {
        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
            this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
        }

        this.currentPieceCoordinates = this.shadowCoordinates;
        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].colour = this.currentPiece.colour;
            this.board[point.x][point.y].state = TetrominoSquare.State.FALLING;
        }

        placePiece();
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
            if (point.y + translation.y >= 24 || point.y + translation.y < 0 || point.x + translation.x >= 10 || point.x + translation.x < 0) {
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

    /**
     * Moves the current piece in the specified direction
     *
     * @param direction the direction to move the piece in
     **/

    public void movePiece(MovementDirection direction) {
        ArrayList<Point> newCoordinates = tryMovePiece(direction);
        if (newCoordinates.isEmpty()) return;

        // Remove the current piece from the board
        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
        }

        // Update the current piece's coordinates
        this.currentPieceCoordinates = newCoordinates;
        if (direction == MovementDirection.DOWN) {
            this.currentPieceTopLeftCorner.y++;
        } else if (direction == MovementDirection.LEFT) {
            this.currentPieceTopLeftCorner.x--;
        } else if (direction == MovementDirection.RIGHT) {
            this.currentPieceTopLeftCorner.x++;
        }

        // Remove the shadow
        for (Point point : shadowCoordinates) {
            if (this.board[point.x][point.y].colour == TetrominoSquare.Colours.SHADOW) {
                this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
            }
        }

        // Update the shadow
        shadowCoordinates = calculateFall();
        renderShadow(shadowCoordinates);

        // Add the current piece to the board
        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].colour = this.currentPiece.colour;
            this.board[point.x][point.y].state = TetrominoSquare.State.FALLING;
        }

        dropLockTime = 0;
        // Increment the drop lock reset count
        dropLockResetCount++;

        if (!tryMovePiece(MovementDirection.DOWN).isEmpty()) {
            // If it can move down, reset the drop lock
            dropLockResetCount = 0;
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

        // Get the kick that was used to rotate the piece
        Point kick = calculateRotation(rotations);

        // null means that the piece cannot be rotated
        if (kick == null) {
            return;
        }

        // Set the rotation
        currentPiece.setRotation(currentPiece.getRotation() + rotations);

        // Remove the current piece from the board
        for (Point point : this.currentPieceCoordinates) {
            this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
            this.board[point.x][point.y].state = TetrominoSquare.State.EMPTY;
        }

        this.currentPieceCoordinates.clear();

        // Add the rotated piece to the board, using the kick
        for (int i = 0; i < currentPiece.squares.length; i++) {
            for (int j = 0; j < currentPiece.squares[i].length; j++) {
                if (currentPiece.squares[i][j].state == TetrominoSquare.State.FALLING) {
                    this.currentPieceCoordinates.add(new Point(j + currentPieceTopLeftCorner.x + kick.x, i + currentPieceTopLeftCorner.y + kick.y));
                    this.board[j + currentPieceTopLeftCorner.x + kick.x][i + currentPieceTopLeftCorner.y + kick.y].colour = currentPiece.colour;
                    this.board[j + currentPieceTopLeftCorner.x + kick.x][i + currentPieceTopLeftCorner.y + kick.y].state = TetrominoSquare.State.FALLING;
                }
            }
        }

        // Update the top left corner
        currentPieceTopLeftCorner.x += kick.x;
        currentPieceTopLeftCorner.y += kick.y;

        // Remove the shadow
        for (Point point : shadowCoordinates) {
            if (this.board[point.x][point.y].colour == TetrominoSquare.Colours.SHADOW) {
                this.board[point.x][point.y].colour = TetrominoSquare.Colours.EMPTY;
            }
        }

        // Update the shadow
        this.shadowCoordinates = calculateFall();
        renderShadow(shadowCoordinates);

        dropLockTime = 0;
        // Increment the drop lock reset count
        dropLockResetCount++;

        // Reset the drop lock if the piece can move down
        if (!tryMovePiece(MovementDirection.DOWN).isEmpty()) {
            dropLockResetCount = 0;
        }
    }

    /**
     * Checks to see if a piece can rotate. Will kick the piece if needed.
     *
     * @param rotations the number of 90 degree rotations.
     *                  1 for clockwise, 3 for counter-clockwise
     * @return null if the piece cannot rotate, kick offset if can
     */
    public Point calculateRotation(int rotations) throws CloneNotSupportedException {
        rotations %= currentPiece.getNumberOfRotationStates();

        if (rotations == 0) {
            return null;
        }

        // Get a new piece with the rotation applied
        TetrominoShape rotated = currentPiece.clone();
        rotated.setRotation(currentPiece.getRotation() + rotations);

        // Calculate the rotation
        Utils.Pair<Integer, Integer> rotation = new Utils.Pair<>(currentPiece.getRotation(), rotated.getRotation());
        Point[] kicks = new Point[]{new Point(0, 0)};

        // Get the kicks
        if (this.currentPiece.shape != TetrominoShape.Shapes.I) {
            kicks = Tables.NON_I_WALL_KICK_DATA.get(rotation);
        } else if (Tables.I_WALL_KICK_DATA.containsKey(rotation)) {
            kicks = Tables.I_WALL_KICK_DATA.get(rotation);
        }

        // Try each kick
        for (Point kick : kicks) {
            boolean canRotate = true;
            OuterLoop:
            for (int i = 0; i < currentPiece.squares.length; i++) {
                for (int j = 0; j < currentPiece.squares[i].length; j++) {
                    if (currentPiece.squares[i][j].state == TetrominoSquare.State.FALLING) {
                        // Out of bounds
                        if (j + currentPieceTopLeftCorner.x + kick.x < 0 || j + currentPieceTopLeftCorner.x + kick.x >= this.board.length || i + currentPieceTopLeftCorner.y + kick.y < 0 || i + currentPieceTopLeftCorner.y + kick.y >= this.board[0].length) {
                            canRotate = false;
                            break OuterLoop;
                        }

                        // Check if obstructed
                        if (this.board[j + currentPieceTopLeftCorner.x + kick.x][i + currentPieceTopLeftCorner.y + kick.y].state == TetrominoSquare.State.PLACED) {
                            canRotate = false;
                            break OuterLoop;
                        }
                    }
                }
            }
            if (canRotate) return kick;
        }
        return null;
    }

    /**
     * Clears all rows that are full
     *
     * @return A list of rows that were cleared
     */
    public ArrayList<Integer> clearLines() {
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
}
