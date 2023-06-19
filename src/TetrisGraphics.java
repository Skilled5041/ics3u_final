// Aaron Ye
// 2023-06-18
// Tetris graphics
// Various methods for rendering the game

import jdk.jshell.execution.Util;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TetrisGraphics {
    public static Clip[] countdownClips = new Clip[4];
    public static Image[] countdownImages = new Image[4];
    public static Image finishImage = Toolkit.getDefaultToolkit().getImage("assets/game_imgs/finish.png");
    public static Clip finish = Utils.getAudio("assets/game_sfx/finish.wav");
    public static int countdown = -1;

    // Animation variables
    private static int countdownAnimationIterCount = 0;
    private static float countdownAnimationScale = 1.0F;
    public static float boardOffsetIterCount = 0;
    public static int boardOffsetY = -400;
    public static boolean finishAnimation = false;
    public static int finishAnimationIterCount = 0;
    private static float finishAnimationScale = 1.0F;
    public static boolean failAnimation = false;
    public static int failAnimationIterCount = 0;
    static Clip failSfxClip = Utils.getAudio("assets/game_sfx/game_over.wav");
    static Image background = Toolkit.getDefaultToolkit().getImage("assets/backgrounds/mm_bg_" + Utils.randomInteger(1, 35) + ".png").getScaledInstance(Game.WIDTH, Game.HEIGHT, Image.SCALE_SMOOTH);


    // Load the countdown images and audio
    static {
        countdownClips[0] = Utils.getAudio("assets/game_sfx/countdown_1.wav");
        countdownClips[1] = Utils.getAudio("assets/game_sfx/countdown_2.wav");
        countdownClips[2] = Utils.getAudio("assets/game_sfx/countdown_3.wav");
        countdownClips[3] = Utils.getAudio("assets/game_sfx/go.wav");

        countdownImages[0] = Toolkit.getDefaultToolkit().getImage("assets/game_imgs/countdown3.png");
        countdownImages[1] = Toolkit.getDefaultToolkit().getImage("assets/game_imgs/countdown2.png");
        countdownImages[2] = Toolkit.getDefaultToolkit().getImage("assets/game_imgs/countdown1.png");
        countdownImages[3] = Toolkit.getDefaultToolkit().getImage("assets/game_imgs/go.png");
    }

    /**
     * Plays an animation when the game starts
     */
    public static void playStartAnimation() {
        // Timer for the countdown animation
        Timer countdownAnimationTimer = new Timer(10, null);
        countdownAnimationTimer.addActionListener(e -> {
            countdownAnimationIterCount += 1;
            float interpolation = Utils.easeOutExpo(countdownAnimationIterCount / 100.0F);
            countdownAnimationScale = 1 - interpolation;
        });

        // Timer for the board movement animation
        Timer boardMovementTimer = new Timer(10, null);
        boardMovementTimer.addActionListener(e -> {
            boardOffsetIterCount += 0.5;
            float interpolation = Utils.easeOutCubic(boardOffsetIterCount / 100.0F);
            boardOffsetY = (int) (-400 * (1 - interpolation));
            if (boardOffsetY >= 0) {
                boardOffsetY = 0;
                boardMovementTimer.stop();
            }
        });

        boardMovementTimer.start();

        AtomicInteger count = new AtomicInteger();
        Timer countdownTimer = new Timer(1000, null);
        countdownAnimationTimer.start();

        // Countdown logic
        countdownTimer.addActionListener(e -> {
            if (count.get() < 4) {
                if (count.get() == 3) {
                    boardMovementTimer.stop();
                    boardOffsetY = 0;
                    boardOffsetIterCount = 0;
                }
                TetrisGraphics.countdownClips[count.get()].setFramePosition(0);
                Utils.playClip(TetrisGraphics.countdownClips[count.get()]);
                count.getAndIncrement();
                countdown++;
                countdownAnimationIterCount = 0;
                countdownAnimationScale = 1.0F;
            } else {
                countdownTimer.stop();
                countdownAnimationTimer.stop();
                countdown = -1;
            }
        });

        countdownTimer.start();
        background = Toolkit.getDefaultToolkit().getImage("assets/backgrounds/mm_bg_" + Utils.randomInteger(1, 35) + ".png").getScaledInstance(Game.WIDTH, Game.HEIGHT, Image.SCALE_SMOOTH);
    }

    /**
     * Plays an animation for when the game ends
     */
    public static void playFinishAnimation() {
        finishAnimation = true;
        finish.setFramePosition(0);
        Utils.playClip(finish);

        // Finish animation
        Timer finishAnimationTimer = new Timer(10, null);
        finishAnimationTimer.addActionListener(e -> {
            finishAnimationIterCount += 1;
            finishAnimationScale = Utils.easeOutExpo(finishAnimationIterCount / 100.0F);
            if (finishAnimationIterCount >= 100) {
                finishAnimationTimer.stop();
                finishAnimationIterCount = 0;
                finishAnimation = false;
            }
            Main.game.repaint();
        });

        finishAnimationTimer.start();
    }

    /**
     * Plays an animation for when the player fails
     */
    public static void playFailAnimation() {
        failAnimation = true;
        failSfxClip.setFramePosition(0);
        Utils.playClip(failSfxClip);
        // Fail animation
        Timer failAnimationTimer = new Timer(10, null);
        failAnimationTimer.addActionListener(e -> {
            failAnimationIterCount += 1.5;
            if (failAnimationIterCount >= 100) {
                failAnimationTimer.stop();
                failAnimationIterCount = 0;
                failAnimation = false;
            }
            Main.game.repaint();
        });

        failAnimationTimer.start();
    }

    public static Map<String, Clip> gameSfx = new HashMap<>() {{
        put("clear_line", Utils.getAudio("assets/game_sfx/clear_line.wav"));
        put("clear_quad", Utils.getAudio("assets/game_sfx/clear_quad.wav"));
        put("danger", Utils.getAudio("assets/game_sfx/danger.wav"));
        put("hard_drop", Utils.getAudio("assets/game_sfx/hard_drop.wav"));
        put("hold", Utils.getAudio("assets/game_sfx/hold.wav"));
        put("move", Utils.getAudio("assets/game_sfx/move.wav"));
        put("rotate", Utils.getAudio("assets/game_sfx/rotate.wav"));
        put("soft_drop", Utils.getAudio("assets/game_sfx/soft_drop.wav"));
        put("perfect_clear", Utils.getAudio("assets/game_sfx/perfect_clear.wav"));
    }};

    /**
     * Plays a sound effect
     *
     * @param sound The sound effect to play
     */
    public static void playSound(String sound) {
        Clip clip = gameSfx.get(sound);
        clip.stop();
        clip.setFramePosition(0);
        Utils.playClip(clip);
    }

    /**
     * Stops a sound effect
     *
     * @param sound The sound effect to stop
     */
    public static void stopSound(String sound) {
        Clip clip = gameSfx.get(sound);
        clip.stop();
    }

    /**
     * Loops a sound effect
     *
     * @param sound The sound effect to loop
     */
    public static void loopSound(String sound) {
        Clip clip = gameSfx.get(sound);
        Utils.loopClip(clip);
    }

    // Maps the piece colours to their respective image file names
    public static Map<TetrominoSquare.Colours, String> pieceImageFileNames = new HashMap<>() {{
        put(TetrominoSquare.Colours.CYAN, "cyan.png");
        put(TetrominoSquare.Colours.BLUE, "blue.png");
        put(TetrominoSquare.Colours.ORANGE, "orange.png");
        put(TetrominoSquare.Colours.YELLOW, "yellow.png");
        put(TetrominoSquare.Colours.GREEN, "green.png");
        put(TetrominoSquare.Colours.PURPLE, "purple.png");
        put(TetrominoSquare.Colours.RED, "red.png");
        put(TetrominoSquare.Colours.SHADOW, "shadow.png");
        put(TetrominoSquare.Colours.DISABLED, "disabled.png");
    }};

    public static Map<TetrominoSquare.Colours, Color> colorsMap = new HashMap<>() {{
        put(TetrominoSquare.Colours.CYAN, new Color(0, 255, 196));
        put(TetrominoSquare.Colours.BLUE, new Color(72, 75, 255));
        put(TetrominoSquare.Colours.ORANGE, new Color(255, 144, 0));
        put(TetrominoSquare.Colours.YELLOW, new Color(206, 206, 80));
        put(TetrominoSquare.Colours.GREEN, new Color(113, 231, 100));
        put(TetrominoSquare.Colours.PURPLE, new Color(215, 112, 255));
        put(TetrominoSquare.Colours.RED, new Color(255, 72, 72));
        put(TetrominoSquare.Colours.SHADOW, new Color(255, 255, 255, 50));
        put(TetrominoSquare.Colours.DISABLED, new Color(42, 40, 40));
    }};

    public static Map<TetrominoSquare.Colours, BufferedImage> pieceImages = new HashMap<>();
    private static final int SQUARE_SIZE = 35;
    public static Clip dangerClip = gameSfx.get("danger");
    public static Color color = new Color(255, 255, 255);

    /**
     * Load the images into the map
     */
    public static void loadImages() {
        MediaTracker tracker = new MediaTracker(Main.frame);

        for (TetrominoSquare.Colours colour : TetrominoSquare.Colours.values()) {
            if (colour == TetrominoSquare.Colours.EMPTY) continue;

            try {
                // Make shadow semi-transparent
                if (colour == TetrominoSquare.Colours.SHADOW) {
                    BufferedImage shadowImage = ImageIO.read(new File("assets/piece_textures/shadow.png"));
                    BufferedImage shadowImageTransparent = new BufferedImage(shadowImage.getWidth(), shadowImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = shadowImageTransparent.createGraphics();
                    graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                    graphics.drawImage(shadowImage, 0, 0, null);
                    graphics.dispose();
                    TetrisGraphics.pieceImages.put(colour, shadowImageTransparent);
                } else {
                    TetrisGraphics.pieceImages.put(colour, ImageIO.read(new File("assets/piece_textures/" + TetrisGraphics.pieceImageFileNames.get(colour))));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tracker.addImage(TetrisGraphics.pieceImages.get(colour), 0);
        }

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Renders the board
     *
     * @param g2d    Graphics2D object
     * @param tetris TetrisBoard object
     */
    public static void renderBoard(Graphics2D g2d, TetrisBoard tetris) {
        float dimAmount = 0.8f;
        // Play danger sound when player's stack is high
        if (tetris.highestPieceRow <= 6 && !dangerClip.isRunning()) {
            dangerClip.setFramePosition(0);
            Utils.loopClip(dangerClip);
            color = new Color(203, 16, 16);
            dimAmount = 0.95f;
        } else if (tetris.highestPieceRow > 6 && dangerClip.isRunning()) {
            dangerClip.stop();
        } else if (tetris.highestPieceRow > 6) {
            color = new Color(255, 255, 255);
        }
        // Draw background
        g2d.drawImage(background, 0, 0, null);
        // Dim it
        g2d.setColor(new Color(0, 0, 0, dimAmount));
        g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
        // Hod piece HUD
        // Top rectangle part
        g2d.setColor(color);
        g2d.fillRect(800 - 5 * SQUARE_SIZE, 50 + 3 * SQUARE_SIZE - 2 + boardOffsetY, 5 * SQUARE_SIZE - 3, 30);
        // Hold text
        g2d.setColor(new Color(0, 0, 0));
        g2d.drawString("HOLD", 800 - 5 * SQUARE_SIZE + 5, 50 + 3 * SQUARE_SIZE + 20 + boardOffsetY);
        // Left line
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(800 - 5 * SQUARE_SIZE, 50 + 3 * SQUARE_SIZE + boardOffsetY, 800 - 5 * SQUARE_SIZE, 65 + 6 * SQUARE_SIZE + boardOffsetY);
        // Diagonal line
        g2d.drawLine(800 - 5 * SQUARE_SIZE + 1, 65 + 6 * SQUARE_SIZE + 2 + boardOffsetY, 780 - 4 * SQUARE_SIZE, 85 + 6 * SQUARE_SIZE + boardOffsetY);
        // Bottom line
        g2d.drawLine(780 - 4 * SQUARE_SIZE, 85 + 6 * SQUARE_SIZE + boardOffsetY, 800 - 3, 85 + 6 * SQUARE_SIZE + boardOffsetY);

        // Piece queue HUD
        // Top rectangle part
        g2d.setColor(color);
        g2d.fillRect(800 + 10 * SQUARE_SIZE + 3, 50 + 3 * SQUARE_SIZE - 2 + boardOffsetY, 5 * SQUARE_SIZE - 3, 30);
        // Queue text
        g2d.setColor(new Color(0, 0, 0));
        g2d.drawString("NEXT", 800 + 10 * SQUARE_SIZE + 5, 50 + 3 * SQUARE_SIZE + 20 + boardOffsetY);
        // Right vertical line
        g2d.setColor(color);
        g2d.drawLine(800 + 15 * SQUARE_SIZE, 50 + 3 * SQUARE_SIZE - 1 + boardOffsetY, 800 + 15 * SQUARE_SIZE, 65 + 18 * SQUARE_SIZE + boardOffsetY);
        // Diagonal line
        g2d.drawLine(800 + 15 * SQUARE_SIZE - 1, 65 + 18 * SQUARE_SIZE + 2 + boardOffsetY, 800 + 14 * SQUARE_SIZE + 20, 85 + 18 * SQUARE_SIZE + boardOffsetY);
        // Bottom line
        g2d.drawLine(800 + 14 * SQUARE_SIZE + 20, 85 + 18 * SQUARE_SIZE + boardOffsetY, 800 + 10 * SQUARE_SIZE + 3, 85 + 18 * SQUARE_SIZE + boardOffsetY);

        // Lines on the side and bottom of the board
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(3));
        // Vertical lines
        g2d.drawLine(800 - 3, 50 + SQUARE_SIZE * 3 + boardOffsetY, 800 - 3, 50 + SQUARE_SIZE * 24 + boardOffsetY);
        g2d.drawLine(800 + 10 * SQUARE_SIZE + 3, 50 + SQUARE_SIZE * 3 + boardOffsetY, 800 + 10 * SQUARE_SIZE + 3, 50 + SQUARE_SIZE * 24 + boardOffsetY);
        // Horizontal line
        g2d.drawLine(800 - 3, 50 + SQUARE_SIZE * 24 + 3 + boardOffsetY, 800 + 10 * SQUARE_SIZE + 3, 50 + SQUARE_SIZE * 24 + 3 + boardOffsetY);

        // Draw grid under the pieces
        // Grid
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(255, 255, 255, 15));
        for (int column = 0; column < 10; column++) {
            for (int row = 3; row < 24; row++) {
                // Grid lines
                g2d.drawRect(column * SQUARE_SIZE + 800, row * SQUARE_SIZE + 50 + boardOffsetY, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        // Draw pieces over the grid
        for (int column = 0; column < 10; column++) {
            for (int row = 0; row < 24; row++) {
                if (tetris.board[column][row].colour == TetrominoSquare.Colours.EMPTY) continue;
                if (Main.useFlatSkin) {
                    g2d.setColor(colorsMap.get(tetris.board[column][row].colour));
                    g2d.fillRect(
                            column * SQUARE_SIZE + 800,
                            row * SQUARE_SIZE + 50 + boardOffsetY,
                            SQUARE_SIZE,
                            SQUARE_SIZE
                    );
                } else {
                    g2d.drawImage(
                            TetrisGraphics.pieceImages.get(tetris.board[column][row].colour),
                            column * SQUARE_SIZE + 800,
                            row * SQUARE_SIZE + 50 + boardOffsetY,
                            SQUARE_SIZE,
                            SQUARE_SIZE,
                            null
                    );
                }
            }
        }

        // Draw the hold piece
        g2d.setColor(color);
        TetrominoShape holdPiece = tetris.holdPiece;

        if (holdPiece != null) {
            int startX;
            if (holdPiece.squares[0].length == 4) {
                startX = 785;
            } else {
                startX = 800;
            }
            int offsetY = 0;
            if (holdPiece.squares.length == 4) offsetY = -SQUARE_SIZE / 2;

            for (int i = 0; i < holdPiece.squares.length; i++) {
                for (int j = 0; j < holdPiece.squares[0].length; j++) {
                    if (holdPiece.squares[i][j].state == TetrominoSquare.State.FALLING) {
                        TetrominoSquare.Colours colour = holdPiece.squares[i][j].colour;
                        if (tetris.alreadyHeld) {
                            colour = TetrominoSquare.Colours.DISABLED;
                        }
                        if (Main.useFlatSkin) {
                            g2d.setColor(colorsMap.get(colour));
                            g2d.fillRect(
                                    (j - 2) * SQUARE_SIZE + startX - 2 * SQUARE_SIZE,
                                    (i - 2) * SQUARE_SIZE + 65 + 6 * SQUARE_SIZE + offsetY + boardOffsetY,
                                    SQUARE_SIZE,
                                    SQUARE_SIZE
                            );
                        } else {
                            g2d.drawImage(
                                    TetrisGraphics.pieceImages.get(colour),
                                    (j - 2) * SQUARE_SIZE + startX - 2 * SQUARE_SIZE,
                                    (i - 2) * SQUARE_SIZE + 65 + 6 * SQUARE_SIZE + offsetY + boardOffsetY,
                                    SQUARE_SIZE,
                                    SQUARE_SIZE,
                                    null
                            );
                        }
                    }
                }
            }
        }

        // Draw the queue
        int count = 0;
        for (TetrominoShape piece : tetris.tetrominoQueue()) {
            int startX;
            if (piece.squares[0].length == 4) {
                startX = 1310;
            } else {
                startX = 1330;
            }
            int offsetY = 0;
            if (piece.squares.length == 4) offsetY = -SQUARE_SIZE / 2;

            for (int i = 0; i < piece.squares.length; i++) {
                for (int j = 0; j < piece.squares[0].length; j++) {
                    if (piece.squares[i][j].state == TetrominoSquare.State.FALLING) {
                        TetrominoSquare.Colours colour = piece.squares[i][j].colour;
                        if (Main.useFlatSkin) {
                            g2d.setColor(colorsMap.get(colour));
                            g2d.fillRect(
                                    (j - 2) * SQUARE_SIZE + startX - 2 * SQUARE_SIZE,
                                    (i - 2) * SQUARE_SIZE + 65 + 6 * SQUARE_SIZE + offsetY + count * 3 * SQUARE_SIZE + boardOffsetY,
                                    SQUARE_SIZE,
                                    SQUARE_SIZE
                            );
                        } else {
                            g2d.drawImage(
                                    TetrisGraphics.pieceImages.get(colour),
                                    (j - 2) * SQUARE_SIZE + startX - 2 * SQUARE_SIZE,
                                    (i - 2) * SQUARE_SIZE + 65 + 6 * SQUARE_SIZE + offsetY + count * 3 * SQUARE_SIZE + boardOffsetY,
                                    SQUARE_SIZE,
                                    SQUARE_SIZE,
                                    null
                            );
                        }
                    }
                }
            }
            count++;
        }

        // Countdown image
        if (countdown != -1 && countdown < 4) {
            Utils.drawCenteredImage(
                    g2d,
                    countdownImages[countdown],
                    980,
                    500,
                    (int) Math.max(0, 400 * countdownAnimationScale),
                    (int) Math.max(0, 400 * countdownAnimationScale)
            );
        }

        if (finishAnimation) {
            // Fade to black
            g2d.setColor(new Color(0, 0, 0, (int) (255 * finishAnimationScale)));
            g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

            // Draw finish image
            Utils.drawCenteredImage(
                    g2d,
                    finishImage,
                    980,
                    500,
                    (int) Math.max(0, 800 * finishAnimationScale),
                    (int) Math.max(0, 400 * finishAnimationScale)
            );

        }

        if (failAnimation) {
            // Fade to black
            g2d.setColor(new Color(0, 0, 0, (int) (255 * (Math.max(failAnimationIterCount, 1) * 0.01))));
            g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
        }
    }
}
