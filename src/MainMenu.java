// Aaron Ye
// 2023-06-18
// Main menu
// Class for the main menu

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class MainMenu implements Game.GameState {

    public static Clip bgmClip;

    static {
        try {
            bgmClip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    AudioInputStream mainMenuBgm = AudioSystem.getAudioInputStream(new File("assets/bgm/other/Aerial City, Chika - Menu Music.wav"));
    Image background = ImageIO.read(new File("assets/backgrounds/mm_bg_" + Utils.randomInteger(1, 35) + ".png")).getScaledInstance(Game.WIDTH, Game.HEIGHT, Image.SCALE_SMOOTH);
    Image fortyLinesIcon = ImageIO.read(new File("assets/icons/40_lines.png"));
    Image blitzIcon = ImageIO.read(new File("assets/icons/blitz.png"));
    Image configIcon = ImageIO.read(new File("assets/icons/config.png"));
    Image aboutIcon = ImageIO.read(new File("assets/icons/about.png"));

    public MainMenu() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        bgmClip.open(mainMenuBgm);
    }

    JPanel buttons;

    @Override
    public void start() {
        buttons = new JPanel();
        bgmClip.setFramePosition(0);
        Utils.loopClip(bgmClip);

        // Create buttons
        Components.MenuButton fortyLinesButton = new Components.MenuButton()
                .setDescriptionText("Complete  40  lines  as  quickly  as  possible")
                .setMainText("40  lines")
                .setIcon(fortyLinesIcon)
                .setDescriptionTextColor(new Color(197, 129, 85))
                .setMainTextColor(new Color(255, 188, 141))
                .setBackgroundColor(new Color(43, 33, 28))
                .setHighlightColor(new Color(74, 49, 32))
                .setShadowColor(new Color(19, 16, 14));

        Components.MenuButton blitzButton = new Components.MenuButton()
                .setDescriptionText("A  Two-Minute  Race  Against  the  Clock")
                .setMainText("Blitz")
                .setIcon(blitzIcon)
                .setDescriptionTextColor(new Color(210, 148, 148))
                .setMainTextColor(new Color(255, 187, 186))
                .setBackgroundColor(new Color(65, 29, 29))
                .setHighlightColor(new Color(112, 45, 44))
                .setShadowColor(new Color(48, 23, 22));

        Components.MenuButton configButton = new Components.MenuButton()
                .setDescriptionText("Tweak  your  tetris  experience")
                .setMainText("Config")
                .setIcon(configIcon)
                .setDescriptionTextColor(new Color(78, 100, 184))
                .setMainTextColor(new Color(132, 172, 250))
                .setBackgroundColor(new Color(27, 37, 61))
                .setHighlightColor(new Color(28, 47, 98))
                .setShadowColor(new Color(14, 15, 19));

        Components.MenuButton aboutButton = new Components.MenuButton()
                .setDescriptionText("View  information  about  the  game")
                .setMainText("About")
                .setIcon(aboutIcon)
                .setDescriptionTextColor(new Color(93, 93, 93))
                .setMainTextColor(new Color(216, 216, 216))
                .setBackgroundColor(new Color(36, 36, 36))
                .setHighlightColor(new Color(43, 43, 43))
                .setShadowColor(new Color(21, 21, 21));


        // Add buttons to panel
        buttons.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(50, 0, 0, 0);

        buttons.add(fortyLinesButton, constraints);
        constraints.insets = new Insets(40, 0, 0, 0);
        buttons.add(blitzButton, constraints);
        buttons.add(configButton, constraints);
        buttons.add(aboutButton, constraints);

        buttons.setOpaque(false);
        buttons.setBounds(0, 0, Game.WIDTH, Game.HEIGHT);

        fortyLinesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Main.game.switchState(Game.GameStates.SPRINT);
                } catch (LineUnavailableException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        blitzButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Main.game.switchState(Game.GameStates.BLITZ);
                } catch (LineUnavailableException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        configButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Main.game.switchState(Game.GameStates.SETTINGS);
                } catch (LineUnavailableException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        aboutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Main.game.switchState(Game.GameStates.ABOUT);
                } catch (LineUnavailableException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        Main.game.add(buttons);
        Main.game.revalidate();
    }

    @Override
    public void end() {
        if (buttons != null) {
            Main.game.remove(buttons);
        }
        Main.game.revalidate();
        bgmClip.stop();
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.drawImage(background, 0, 0, null);
        // Dim the background
        g2d.setColor(new Color(0, 0, 0, 0.85f));
        g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
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
