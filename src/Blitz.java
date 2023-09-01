// Aaron Ye
// 2023-06-18
// Blitz State
// Class for the blitz game mode

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;

public class Blitz implements Game.GameState {
    private static TetrisBoard tetris;

    // Time left in the game
    public static int timeLeftMs = 120000;
    public static Clip bgm;

    // UNIX time
    long gameStartTime = 0;
    public static int score = 0;

    // Runs every frame
    Timer frameTimer;
    Timer dasArrSdfTimer = new Timer(1, null);
    TetrisBoard.MovementDirection lastMovementDirection = null;

    @Override
    public void start() {
        Main.frame.requestFocus();
        bgm = Utils.setVolume(Objects.requireNonNull(Utils.randomAudio("assets/bgm/" + Main.musicType)), 0.5F);
        tetris = new TetrisBoard();
        // Two minutes
        timeLeftMs = 120000;
        TetrisGraphics.playStartAnimation();

        // Spawn tetromino after 4 seconds
        Timer initialSpawn = new Timer(4000, null);
        initialSpawn.addActionListener(e -> {
            gameStartTime = System.currentTimeMillis();
            tetris.spawnNewTetromino();
            tetris.gameStarted = true;
            Utils.loopClip(bgm);
            initialSpawn.stop();
        });

        initialSpawn.start();
        dasArrSdfTimer.addActionListener((e) -> {
            // DAS / ARR logic
            if (lastMovementDirection == TetrisBoard.MovementDirection.LEFT || lastMovementDirection == TetrisBoard.MovementDirection.RIGHT) {
                if (tetris.dasTime != -1 && tetris.dasTime + TetrisBoard.das <= System.currentTimeMillis()
                        && TetrisBoard.arr == 0) {
                    while (!tetris.tryMovePiece(lastMovementDirection).isEmpty()) {
                        tetris.movePiece(lastMovementDirection);
                    }
                } else if (tetris.dasTime != -1 && tetris.dasTime + TetrisBoard.das <= System.currentTimeMillis()
                        && System.currentTimeMillis() - tetris.lastDasTime >= TetrisBoard.arr) {
                    TetrisGraphics.playSound("move");
                    tetris.movePiece(lastMovementDirection);
                    tetris.lastDasTime = System.currentTimeMillis();
                }
            } else if (lastMovementDirection == TetrisBoard.MovementDirection.DOWN && tetris.softDropping) {
                if (TetrisBoard.sdf == 0) {
                    while (!tetris.tryMovePiece(lastMovementDirection).isEmpty()) {
                        tetris.movePiece(lastMovementDirection);
                    }
                } else if (tetris.lastSdfTime + TetrisBoard.sdf <= System.currentTimeMillis() && tetris.lastSdfTime != -1) {
                    tetris.movePiece(lastMovementDirection);
                    tetris.lastSdfTime = System.currentTimeMillis();
                    TetrisGraphics.playSound("soft_drop");
                }
            }
        });

        dasArrSdfTimer.start();

        // Run every frame
        frameTimer = new Timer(1000 / 60, e -> {
            if (tetris.gameStarted) {
                timeLeftMs = 120000 - (int) (System.currentTimeMillis() - gameStartTime);
            }
            tetris.gameFrame();
            Main.frame.repaint();

            // Check if player is alive
            if (tetris.toppedOut) {
                tetris.gameEnded = true;
                tetris.endGame();
                frameTimer.stop();
                dasArrSdfTimer.stop();
                TetrisGraphics.playFailAnimation();
                Timer switchStateTimer = new Timer(1000, null);
                switchStateTimer.addActionListener(e1 -> {
                    try {
                        Main.game.switchState(Game.GameStates.BLITZ_FAIL);
                    } catch (LineUnavailableException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    switchStateTimer.stop();
                });

                switchStateTimer.start();
            }

            // Check if time is up
            if (timeLeftMs <= 0) {
                tetris.gameEnded = true;
                tetris.endGame();
                score = tetris.score();
                frameTimer.stop();
                dasArrSdfTimer.stop();
                TetrisGraphics.playFinishAnimation();
                Timer switchStateTimer = new Timer(1000, null);
                switchStateTimer.addActionListener(e1 -> {
                    try {
                        Main.game.switchState(Game.GameStates.BLITZ_END);
                    } catch (LineUnavailableException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    switchStateTimer.stop();
                });

                switchStateTimer.start();
            }
        });
        frameTimer.start();

    }

    @Override
    public void end() {
        TetrisGraphics.dangerClip.stop();
        bgm.stop();
        tetris.gameStarted = false;
        tetris.gameEnded = true;
        tetris.endGame();
        frameTimer.stop();
        dasArrSdfTimer.stop();
        tetris.dasTime = -1;
        tetris.lastDasTime = -1;
        tetris.lastSdfTime = -1;
    }

    @Override
    public void render(Graphics2D g2d) {
        // Fade text out
        TetrisGraphics.renderBoard(g2d, tetris);
        float opacity = 0f;
        if (TetrisGraphics.finishAnimation) {
            opacity = Utils.easeOutExpo(Math.max(TetrisGraphics.finishAnimationIterCount, 1) / 100.0F);
        } else if (TetrisGraphics.failAnimation) {
            opacity = Utils.easeOutExpo(Math.max(TetrisGraphics.failAnimationIterCount, 1) / 100.0F);
        }
        // Score text
        g2d.setColor(new Color(255, 255, 255, 255 - (int) (255 * Math.max(0, Math.min(1, opacity)))));
        g2d.setFont(Game.font24);
        g2d.drawString("Score", 725, 750 + TetrisGraphics.boardOffsetY);
        g2d.setFont(Game.font36);
        g2d.drawString(String.valueOf(tetris.score()), 765 + ((int) Math.log10(tetris.score())) * -20, 790 + TetrisGraphics.boardOffsetY);

        // Time
        g2d.setFont(Game.font24);
        g2d.drawString("Time", 735, 850 + TetrisGraphics.boardOffsetY);
        if (timeLeftMs < 3600000 && timeLeftMs > 0) {
            g2d.drawString(String.format("%1d:%02d:%03d",
                            timeLeftMs / 60000,
                            (timeLeftMs / 1000) % 60,
                            timeLeftMs % 1000),
                    695, 890 + TetrisGraphics.boardOffsetY);
        } else {
            g2d.drawString("0:00:000", 695, 890 + TetrisGraphics.boardOffsetY);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!tetris.gameStarted || tetris.gameEnded) return;
        try {
            // Controls
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> {
                    if (tetris.dasTime == -1) tetris.dasTime = System.currentTimeMillis();
                    else return;
                    TetrisGraphics.playSound("move");
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT -> {
                            lastMovementDirection = TetrisBoard.MovementDirection.LEFT;
                            tetris.movePiece(TetrisBoard.MovementDirection.LEFT);
                        }
                        case KeyEvent.VK_RIGHT -> {
                            lastMovementDirection = TetrisBoard.MovementDirection.RIGHT;
                            tetris.movePiece(TetrisBoard.MovementDirection.RIGHT);
                        }
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (tetris.softDropping) return;
                    lastMovementDirection = TetrisBoard.MovementDirection.DOWN;
                    tetris.movePiece(TetrisBoard.MovementDirection.DOWN);
                    tetris.lastSdfTime = System.currentTimeMillis();
                    tetris.softDropping = true;
                    TetrisGraphics.playSound("soft_drop");
                }
                case KeyEvent.VK_SPACE -> {
                    tetris.hardDrop();
                    TetrisGraphics.playSound("hard_drop");
                }
                case KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C -> {
                    TetrisGraphics.playSound("rotate");
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_Z -> tetris.rotatePiece(3);
                        case KeyEvent.VK_X -> tetris.rotatePiece(2);
                        case KeyEvent.VK_C -> tetris.rotatePiece(1);
                    }
                }
                case KeyEvent.VK_SHIFT -> tetris.hold();
                case KeyEvent.VK_ESCAPE -> {
                    // Exit game
                    tetris.gameEnded = true;
                    tetris.endGame();
                    frameTimer.stop();
                    dasArrSdfTimer.stop();
                    Main.game.switchState(Game.GameStates.MAIN_MENU);
                }
                case KeyEvent.VK_BACK_QUOTE -> {
                    // Restart
                    tetris.gameEnded = true;
                    tetris.endGame();
                    frameTimer.stop();
                    dasArrSdfTimer.stop();
                    Main.game.switchState(Game.GameStates.BLITZ);
                }
            }

            Main.frame.repaint();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> {
                tetris.dasTime = -1;
                tetris.lastDasTime = -1;
            }
            case KeyEvent.VK_DOWN -> {
                tetris.softDropping = false;
                tetris.lastSdfTime = -1;
            }
        }
    }
}
