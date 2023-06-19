// Aaron Ye
// 2023-06-18
// Fail Screen
// A customisable fail screen that displays when the player loses

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class FailScreen implements Game.GameState {
    static MediaTracker tracker = new MediaTracker(Main.frame);
    static Image fortyLinesIcon = Toolkit.getDefaultToolkit().getImage("assets/icons/40_lines.png");
    static Image tetrioIcon = Toolkit.getDefaultToolkit().getImage("assets/icons/tetrio.png").getScaledInstance(170, 170, Image.SCALE_SMOOTH);

    // Load images
    static {
        tracker.addImage(fortyLinesIcon, 1);
        tracker.addImage(tetrioIcon, 2);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Game.GameStates state;
    Components.MenuButton retryButton;

    /**
     * Creates a new fail screen
     * @param state The state to switch to when the retry button is pressed
     * @param retryButton The retry button
     */
    public FailScreen(Game.GameStates state, Components.MenuButton retryButton) {
        this.state = state;
        this.retryButton = retryButton;
    }

    JPanel buttons;

    @Override
    public void start() {
        buttons = new JPanel();

        // Create the buttons
        Components.MenuButton backButton = new Components.MenuButton()
                .setDescriptionText("Return  to  the  main  menu")
                .setMainText("Back")
                .setDescriptionTextColor(new Color(201, 201, 201))
                .setMainTextColor(new Color(255, 255, 255))
                .setBackgroundColor(new Color(36, 36, 36))
                .setHighlightColor(new Color(61, 61, 61))
                .setShadowColor(new Color(24, 24, 24))
                .setIcon(tetrioIcon)
                .setType(Components.MenuButton.ButtonTypes.BACK);

        retryButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Switch to the game state
                try {
                    Main.game.switchState(state);
                } catch (LineUnavailableException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Switch to the main menu
                try {
                    Main.game.switchState(Game.GameStates.MAIN_MENU);
                } catch (LineUnavailableException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Set the layout
        buttons.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(450, 0, 0, 0);

        buttons.add(retryButton, constraints);
        constraints.insets = new Insets(20, 0, 0, 0);
        buttons.add(backButton, constraints);
        buttons.setOpaque(false);
        buttons.setBounds(0, 0, Game.WIDTH, Game.HEIGHT);

        Main.game.add(buttons);
        Main.game.revalidate();
    }

    @Override
    public void end() {
        if (buttons != null) {
            Main.game.remove(buttons);
        }
        Main.game.revalidate();
    }

    @Override
    public void render(Graphics2D g2d) {
        // Show the time
        // Box background
        g2d.setColor(new Color(32, 26, 35));
        g2d.fillRect(500, 200, 900, 250);
        // Highlights
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(new Color(59, 46, 63));
        // Top
        g2d.drawLine(500, 200, 1400 - 3, 200);
        // Left
        g2d.drawLine(500, 200, 500, 450 - 3);
        // Shadow
        g2d.setColor(new Color(10, 8, 11));
        // Bottom
        g2d.drawLine(500 + 3, 450, 1400, 450);
        // Right
        g2d.drawLine(1400, 200 + 3, 1400, 450);
        // Text
        g2d.setColor(new Color(221, 200, 229));
        g2d.setFont(Game.font90);
        g2d.drawString("You  Failed", 740, 350);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
