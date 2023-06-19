// Aaron Ye
// 2023-06-18
// Game manager
// Manages state and other game logic

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Game extends JPanel implements KeyListener {

    /**
     * A game state / screen
     */
    interface GameState extends KeyListener {
        /**
         * Called when the game transitions to this state
         */
        void start() throws LineUnavailableException, IOException;

        /**
         * Called when the game transitions away from this state
         */
        void end();

        /**
         * Called every frame to render the state
         *
         * @param g2d Graphics2d object
         */
        void render(Graphics2D g2d);
    }

    // Different game states
    public enum GameStates {
        MAIN_MENU, SETTINGS, SPRINT, BLITZ, SPRINT_END, BLITZ_END, SPRINT_FAIL, BLITZ_FAIL, ABOUT
    }

    // Size of the window
    public static int WIDTH = 1920;
    public static int HEIGHT = 1080;

    /**
     * Gets the font used in the game
     *
     * @param size Font size
     * @param bold Whether the font should be bold
     * @return Font object
     */
    public static Font getFont(int size, boolean bold) {
        return new Font("HUN-din 1451", bold ? Font.BOLD : Font.PLAIN, size);
    }

    public static Font font24 = getFont(24, true);
    public static Font font32 = getFont(32, true);
    public static Font font36 = getFont(36, true);
    public static Font font40 = getFont(48, true);
    public static Font font64 = getFont(64, true);
    public static Font font80 = getFont(80, true);
    public static Font font90 = getFont(90, true);


    // The duration of a single frame in ms
    public static final int FRAME_TIME = 1000 / 60;
    GameStates currentState = GameStates.MAIN_MENU;

    // Maps game states to classes / objects
    Map<GameStates, GameState> states = new HashMap<>() {{
        try {
            put(GameStates.MAIN_MENU, new MainMenu());
            put(GameStates.SPRINT, new Sprint());
            put(GameStates.SPRINT_END, new SprintEnd());
            put(GameStates.BLITZ, new Blitz());
            put(GameStates.BLITZ_END, new BlitzEnd());
            put(GameStates.SPRINT_FAIL, new FailScreen(GameStates.SPRINT,
                    new Components.MenuButton()
                            .setDescriptionText("Complete  40  lines  as  quickly  as  possible")
                            .setMainText("Retry")
                            .setDescriptionTextColor(new Color(197, 129, 85))
                            .setMainTextColor(new Color(255, 188, 141))
                            .setBackgroundColor(new Color(43, 33, 28))
                            .setHighlightColor(new Color(74, 49, 32))
                            .setShadowColor(new Color(19, 16, 14))
                            .setIcon(SprintEnd.fortyLinesIcon)));
            put(GameStates.BLITZ_FAIL, new FailScreen(GameStates.BLITZ, new Components.MenuButton()
                    .setDescriptionText("A  Two-Minute  Race  Against  the  Clock")
                    .setMainText("Retry")
                    .setIcon(BlitzEnd.blitzIcon)
                    .setDescriptionTextColor(new Color(210, 148, 148))
                    .setMainTextColor(new Color(255, 187, 186))
                    .setBackgroundColor(new Color(65, 29, 29))
                    .setHighlightColor(new Color(112, 45, 44))
                    .setShadowColor(new Color(48, 23, 22))));
            put(GameStates.SETTINGS, new Settings());
            put(GameStates.ABOUT, new About());
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }};

    Font bold = new Font("HUN-din 1451", Font.BOLD, 24);

    public Game() throws LineUnavailableException {
        setFont(bold);
        setDoubleBuffered(true);
        setBackground(new Color(9, 9, 9));

        setPreferredSize(new Dimension(Game.WIDTH, Game.HEIGHT));
        setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 20));

        TetrisGraphics.loadImages();
    }

    /**
     * Switches the current game state
     *
     * @param state New game state
     */
    public void switchState(GameStates state) throws LineUnavailableException, IOException {
        states.get(currentState).end();
        currentState = state;
        states.get(currentState).start();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) (g);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Render the game state
        states.get(currentState).render(g2d);
    }

    // Handle the key events for the current game state
    @Override
    public void keyTyped(KeyEvent e) {
        states.get(currentState).keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        states.get(currentState).keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        states.get(currentState).keyReleased(e);
    }
}
