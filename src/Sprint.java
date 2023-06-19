// Aaron Ye
// 2023-06-18
// Sprint State
// Class for the sprint game mode

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;

public class Sprint implements Game.GameState {
    private static TetrisBoard tetris;
    public static int timeMs = 0;

    // UNIX time
    long gameStartTime = 0;
    Timer frameTimer;
    Timer dasArrSdfTimer = new Timer(1, null);
    TetrisBoard.MovementDirection lastMovementDirection = null;
    Clip bgm;

    @Override
    public void start() {
        bgm = Utils.setVolume(Objects.requireNonNull(Utils.randomAudio("assets/bgm/" + Main.musicType)), 0.5F);
        Main.frame.requestFocus();
        tetris = new TetrisBoard();
        timeMs = 0;
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
                timeMs = (int) (System.currentTimeMillis() - gameStartTime);
            }
            tetris.gameFrame();
            Main.frame.repaint();

            // Handle player victory
            if (tetris.linesCleared >= 40) {
                dasArrSdfTimer.stop();

                tetris.gameEnded = true;
                frameTimer.stop();
                TetrisGraphics.playFinishAnimation();
                Timer switchStateTimer = new Timer(1000, null);
                switchStateTimer.addActionListener(e1 -> {
                    try {
                        Main.game.switchState(Game.GameStates.SPRINT_END);
                    } catch (LineUnavailableException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    switchStateTimer.stop();
                });

                switchStateTimer.start();
            }

            // Handle player death
            if (tetris.toppedOut) {
                dasArrSdfTimer.stop();
                tetris.gameEnded = true;
                frameTimer.stop();
                TetrisGraphics.playFailAnimation();
                Timer switchStateTimer = new Timer(1000, null);
                switchStateTimer.addActionListener(e1 -> {
                    try {
                        Main.game.switchState(Game.GameStates.SPRINT_FAIL);
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
        TetrisGraphics.renderBoard(g2d, tetris);
        // Lines cleared text
        g2d.setColor(new Color(255, 255, 255, 255 - (int) (255 * Math.max(0, Math.min(1, opacity)))));
        g2d.setFont(Game.font24);
        g2d.drawString("Lines", 725, 750 + TetrisGraphics.boardOffsetY);
        g2d.setFont(Game.font24);
        g2d.drawString(String.valueOf(tetris.linesCleared), 725 + ((int) Math.log10(tetris.linesCleared)) * -20, 790 + TetrisGraphics.boardOffsetY);
        g2d.setFont(Game.font24);
        g2d.drawString("/40", 750, 790 + TetrisGraphics.boardOffsetY);

        // Time
        g2d.setFont(Game.font24);
        g2d.drawString("Time", 735, 850 + TetrisGraphics.boardOffsetY);
        // Less than 1 hour
        if (timeMs < 3600000) {
            g2d.drawString(String.format("%02d:%02d:%03d",
                            timeMs / 60000,
                            (timeMs / 1000) % 60,
                            timeMs % 1000),
                    680, 890 + TetrisGraphics.boardOffsetY);
        }
        // More than 1 hour
        // I don't expect people to take longer than an hour
        else {
            g2d.drawString(String.format("%02d:%02d:%02d:%03d",
                            timeMs / 3600000,
                            (timeMs / 60000) % 60,
                            (timeMs / 1000) % 60,
                            timeMs % 1000),
                    650, 890 + TetrisGraphics.boardOffsetY);
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
                case KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E -> {
                    TetrisGraphics.playSound("rotate");
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_Q -> tetris.rotatePiece(3);
                        case KeyEvent.VK_W -> tetris.rotatePiece(2);
                        case KeyEvent.VK_E -> tetris.rotatePiece(1);
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
