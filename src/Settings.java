// Aaron Ye
// 2023-06-18
// Settings state
// Class for the settings menu

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;

public class Settings implements Game.GameState {

    JPanel buttons;
    Image background = BlitzEnd.background;

    @Override
    public void start() throws LineUnavailableException, IOException {
        buttons = new JPanel();

        // Create the button and toggle switch objects
        Components.MenuButton backButton = new Components.MenuButton()
                .setDescriptionText("Return  to  the  main  menu")
                .setMainText("Back")
                .setDescriptionTextColor(new Color(201, 201, 201))
                .setMainTextColor(new Color(255, 255, 255))
                .setBackgroundColor(new Color(36, 36, 36))
                .setHighlightColor(new Color(61, 61, 61))
                .setShadowColor(new Color(24, 24, 24))
                .setIcon(SprintEnd.tetrioIcon)
                .setType(Components.MenuButton.ButtonTypes.BACK);

        Components.ToggleSwitch soundToggle = new Components.ToggleSwitch()
                .setShadowColor(new Color(14, 15, 19))
                .setBackgroundColor(new Color(27, 37, 61))
                .setHighlightColor(new Color(28, 47, 98))
                .setDisabledText("Off")
                .setEnabledText("On")
                .setTextColor(new Color(132, 172, 251))
                .setState(Main.soundOn);

        Components.ToggleSwitch skinToggle = new Components.ToggleSwitch()
                .setShadowColor(new Color(14, 15, 19))
                .setBackgroundColor(new Color(27, 37, 61))
                .setHighlightColor(new Color(28, 47, 98))
                .setDisabledText("Off")
                .setEnabledText("On")
                .setTextColor(new Color(132, 172, 251))
                .setState(Main.useFlatSkin);


        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Main.game.switchState(Game.GameStates.MAIN_MENU);
                } catch (LineUnavailableException | IOException lineUnavailableException) {
                    lineUnavailableException.printStackTrace();
                }
            }
        });

        // Change file when toggled
        soundToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    PrintWriter writer = new PrintWriter("config.txt");
                    writer.println(soundToggle.getState());
                    writer.println(skinToggle.getState());
                    writer.close();
                    Main.soundOn = soundToggle.getState();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        skinToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    PrintWriter writer = new PrintWriter("config.txt");
                    writer.println(soundToggle.getState());
                    writer.println(skinToggle.getState());
                    writer.close();
                    Main.useFlatSkin = skinToggle.getState();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        // Add the buttons to the panel
        buttons.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(200, 0, 0, 0);

        buttons.add(backButton, constraints);
        constraints.insets = new Insets(20, 0, 0, 0);
        buttons.add(soundToggle, constraints);
        buttons.add(skinToggle, constraints);
        buttons.setOpaque(false);
        buttons.setBounds(0, 0, Game.WIDTH, Game.HEIGHT);

        Main.game.add(buttons);
        Main.game.revalidate();
    }

    @Override
    public void end() {
        Main.game.remove(buttons);
        Main.game.revalidate();
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.drawImage(background, 0, 0, Game.WIDTH, Game.HEIGHT, null);
        // Dim the background
        g2d.setColor(new Color(0, 0, 0, 0.8f));
        g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

        // Sound text box
        g2d.setColor(new Color(27, 37, 61));
        g2d.fillRect(500, 440, 1260, 100);
        // Highlights
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(new Color(28, 47, 98));
        g2d.drawLine(500, 440, 1760 - 3, 440);
        g2d.drawLine(500, 440, 500, 540 - 3);
        // Shadow
        g2d.setColor(new Color(14, 15, 19));
        g2d.drawLine(500, 540, 1760, 540);
        g2d.drawLine(1760, 440, 1760, 540);
        // Text
        g2d.setColor(new Color(132, 172, 251));
        g2d.setFont(Game.font40);
        g2d.drawString("Sound", 600, 500);

        // Skin text box
        g2d.setColor(new Color(27, 37, 61));
        g2d.fillRect(500, 560, 1260, 100);
        // Highlights
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(new Color(28, 47, 98));
        g2d.drawLine(500, 560, 1760 - 3, 560);
        g2d.drawLine(500, 560, 500, 660 - 3);
        // Shadow
        g2d.setColor(new Color(14, 15, 19));
        g2d.drawLine(500, 660, 1760, 660);
        g2d.drawLine(1760, 560, 1760, 660);
        // Text
        g2d.setColor(new Color(132, 172, 251));
        g2d.setFont(Game.font40);
        g2d.drawString("Use  Flat  Skin", 600, 620);
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
