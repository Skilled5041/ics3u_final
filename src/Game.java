import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Game extends JPanel implements KeyListener {

    Map<TetrominoSquare.Colours, String> pieceImageFileNames = new HashMap<>() {{
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

    Map<TetrominoSquare.Colours, BufferedImage> pieceImages = new HashMap<>();

    TetrisBoard tetris;

    public static final int FRAME_TIME = 1000 / 60;
    private Timer frameTimer = new Timer(1000 / 60, e -> {
        tetris.gameFrame();
        repaint();
    });
    private final int SQUARE_SIZE = 28;

    Font bold = new Font("HUN-din 1451", Font.BOLD, 20);
    Font regular = new Font("HUN-din 1451", Font.PLAIN, 20);

    public Game() {
        MediaTracker tracker = new MediaTracker(this);

        setFont(bold);
        setDoubleBuffered(true);

        for (TetrominoSquare.Colours colour : TetrominoSquare.Colours.values()) {
            if (colour == TetrominoSquare.Colours.EMPTY) continue;

            try {
                // Make shadow semi-transparent
                if (colour == TetrominoSquare.Colours.SHADOW) {
                    BufferedImage shadowImage = ImageIO.read(new File("assets/textures/shadow.png"));
                    BufferedImage shadowImageTransparent = new BufferedImage(shadowImage.getWidth(), shadowImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = shadowImageTransparent.createGraphics();
                    graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                    graphics.drawImage(shadowImage, 0, 0, null);
                    graphics.dispose();
                    pieceImages.put(colour, shadowImageTransparent);
                } else {
                    pieceImages.put(colour, ImageIO.read(new File("assets/textures/" + pieceImageFileNames.get(colour))));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tracker.addImage(pieceImages.get(colour), 0);
        }

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setBackground(new Color(30, 30, 30));

        tetris = new TetrisBoard();
        tetris.spawnNewTetromino();
        frameTimer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) (g);
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Hod piece HUD
        // Top rectangle part
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRect(650 - 5 * SQUARE_SIZE, 50 + 3 * SQUARE_SIZE - 2, 5 * SQUARE_SIZE - 3, 30);
        // Hold text
        g2d.setColor(new Color(0, 0, 0));
        g2d.drawString("HOLD", 650 - 5 * SQUARE_SIZE + 5, 50 + 3 * SQUARE_SIZE + 20);
        // Left line        
        g2d.setColor(new Color(255, 255, 255));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(650 - 5 * SQUARE_SIZE, 50 + 3 * SQUARE_SIZE, 650 - 5 * SQUARE_SIZE, 65 + 6 * SQUARE_SIZE);
        // Diagonal line
        g2d.drawLine(650 - 5 * SQUARE_SIZE + 1, 65 + 6 * SQUARE_SIZE, 640 - 4 * SQUARE_SIZE, 85 + 6 * SQUARE_SIZE);
        // Bottom line
        g2d.drawLine(640 - 4 * SQUARE_SIZE, 85 + 6 * SQUARE_SIZE, 650 - 3, 85 + 6 * SQUARE_SIZE);
        
        // Piece queue HUD
        // Top rectangle part
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRect(650 + 10 * SQUARE_SIZE + 3, 50 + 3 * SQUARE_SIZE - 2, 5 * SQUARE_SIZE - 3, 30);

        // Lines on the side and bottom of the board
        g2d.setColor(new Color(255, 255, 255));
        g2d.setStroke(new BasicStroke(3));
        // Vertical lines
        g2d.drawLine(650 - 3, 50 + SQUARE_SIZE * 3, 650 - 3, 50 + SQUARE_SIZE * 24);
        g2d.drawLine(650 + 10 * SQUARE_SIZE + 3, 50 + SQUARE_SIZE * 3, 650 + 10 * SQUARE_SIZE + 3, 50 + SQUARE_SIZE * 24);
        // Horizontal line
        g2d.drawLine(650 - 3, 50 + SQUARE_SIZE * 24 + 3, 650 + 10 * SQUARE_SIZE + 3, 50 + SQUARE_SIZE * 24 + 3);

        // Grid
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(255, 255, 255, 10));

        // Draw grid under the pieces
        for (int column = 0; column < 10; column++) {
            for (int row = 3; row < 24; row++) {
                // Grid lines
                g2d.drawRect(column * SQUARE_SIZE + 650, row * SQUARE_SIZE + 50, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        // Draw pieces over the grid
        for (int column = 0; column < 10; column++) {
            for (int row = 0; row < 24; row++) {
                g2d.drawImage(
                        pieceImages.get(tetris.board[column][row].colour),
                        column * SQUARE_SIZE + 650,
                        row * SQUARE_SIZE + 50,
                        SQUARE_SIZE,
                        SQUARE_SIZE,
                        null
                );
            }
        }

        // Draw the hold piece
        TetrominoShape holdPiece = tetris.holdPiece;

        if (holdPiece != null) {
            int startX;
            if (holdPiece.squares[0].length == 4) {
                startX = 635;
            } else {
                startX = 650;
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
                        g2d.drawImage(
                                pieceImages.get(colour),
                                (j - 2) * SQUARE_SIZE + startX - 2 * SQUARE_SIZE,
                                (i - 2) * SQUARE_SIZE + 65 + 6 * SQUARE_SIZE + offsetY,
                                SQUARE_SIZE,
                                SQUARE_SIZE,
                                null
                        );
                    }
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO: Fix this
        try {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                tetris.movePiece(TetrisBoard.MovementDirection.LEFT);
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                tetris.movePiece(TetrisBoard.MovementDirection.RIGHT);
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                tetris.movePiece(TetrisBoard.MovementDirection.DOWN);
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                tetris.hardDrop();
            } else if (e.getKeyCode() == KeyEvent.VK_Q) {
                tetris.rotatePiece(3);
            } else if (e.getKeyCode() == KeyEvent.VK_E) {
                tetris.rotatePiece(1);
            } else if (e.getKeyCode() == KeyEvent.VK_W) {
                tetris.rotatePiece(2);
            } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                tetris.hold();
            }
            repaint();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");

        Game game = new Game();

        frame.add(game);
        frame.addKeyListener(game);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
