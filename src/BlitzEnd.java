// Aaron Ye
// 2023-06-18
// Blitz End State
// Shown when blitz mode is over

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
import java.io.File;
import java.io.IOException;

public class BlitzEnd implements Game.GameState {

    static Clip resultBgmClip = Utils.getAudio("assets/bgm/other/Philosophy, L Side - Blitz Results Screen Music.wav");
    static MediaTracker tracker = new MediaTracker(Main.frame);
    public static Image background;
    static Image blitzIcon = Toolkit.getDefaultToolkit().getImage("assets/icons/blitz.png");
    static Image tetrioIcon = Toolkit.getDefaultToolkit().getImage("assets/icons/tetrio.png").getScaledInstance(170, 170, Image.SCALE_SMOOTH);

    // Load images
    static {
        tracker.addImage(background, 0);
        tracker.addImage(blitzIcon, 1);
        tracker.addImage(tetrioIcon, 2);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            background = ImageIO.read(new File("assets/backgrounds/mm_bg_" + Utils.randomInteger(1, 35) + ".png")).getScaledInstance(Game.WIDTH, Game.HEIGHT, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    JPanel buttons;

    @Override
    public void start() {
        buttons = new JPanel();
        resultBgmClip.setFramePosition(0);
        Utils.playClip(resultBgmClip);

        // Create buttons
        Components.MenuButton againButton = new Components.MenuButton()
                .setDescriptionText("A  Two-Minute  Race  Against  the  Clock")
                .setMainText("Blitz")
                .setIcon(blitzIcon)
                .setDescriptionTextColor(new Color(210, 148, 148))
                .setMainTextColor(new Color(255, 187, 186))
                .setBackgroundColor(new Color(65, 29, 29))
                .setHighlightColor(new Color(112, 45, 44))
                .setShadowColor(new Color(48, 23, 22));

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

        againButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Main.game.switchState(Game.GameStates.BLITZ);
                } catch (LineUnavailableException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Main.game.switchState(Game.GameStates.MAIN_MENU);
                } catch (LineUnavailableException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Set the button layouts
        buttons.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(450, 0, 0, 0);

        buttons.add(againButton, constraints);
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
        resultBgmClip.stop();
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.drawImage(background, 0, 0, null);
        // Dim the background
        g2d.setColor(new Color(0, 0, 0, 0.8f));
        g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

        // Show the time
        // Box background
        g2d.setColor(new Color(65, 37, 36));
        g2d.fillRect(500, 200, 900, 250);
        // Highlights
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(new Color(126, 70, 70));
        // Top
        g2d.drawLine(500, 200, 1400 - 3, 200);
        // Left
        g2d.drawLine(500, 200, 500, 450 - 3);
        // Shadow
        g2d.setColor(new Color(37, 22, 22));
        // Bottom
        g2d.drawLine(500 + 3, 450, 1400, 450);
        // Right
        g2d.drawLine(1400, 200 + 3, 1400, 450);
        // Inner box background
        g2d.setColor(new Color(56, 32, 31));
        g2d.fillRect(640, 270, 710, 160);
        // Title
        g2d.setColor(new Color(225, 162, 162));
        g2d.setFont(Game.font36);
        g2d.drawString("Final  Score", 640, 250);
        // Show the score
        g2d.setColor(new Color(248, 198, 198));
        g2d.setFont(Game.font90);
        int offset = Blitz.score == 0 ? 1 : (int) Math.log(Blitz.score);
        g2d.drawString(String.format("%,d", Blitz.score), 1000 - offset * 15, 390);
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
