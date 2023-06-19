// Aaron Ye
// 2023-06-18
// About state
// Information about the game

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JPanel;
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

public class About implements Game.GameState {

    static Image background = Toolkit.getDefaultToolkit().getImage("assets/backgrounds/mm_bg_" + Utils.randomInteger(1, 35) + ".png");
    static MediaTracker tracker = new MediaTracker(Main.frame);

    static {
        tracker.addImage(background, 0);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    JPanel buttons;

    @Override
    public void start() throws LineUnavailableException, IOException {
        Utils.loopClip(MainMenu.bgmClip);
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

        // Format the layout
        buttons.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(20, 0, 0, 0);
        buttons.add(backButton, constraints);
        buttons.setOpaque(false);
        buttons.setBounds(0, 0, Game.WIDTH, Game.HEIGHT);

        Main.game.add(buttons);
        Main.game.revalidate();
    }

    @Override
    public void end() {
        MainMenu.bgmClip.stop();
        if (buttons != null) {
            Main.game.remove(buttons);
        }
        Main.game.revalidate();
    }

    @Override
    public void render(Graphics2D g2d) {
        // Background
        g2d.drawImage(background, 0, 0, Game.WIDTH, Game.HEIGHT, null);
        // Dim the background
        g2d.setColor(new Color(0, 0, 0, 0.8f));
        g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

        // How to play
        g2d.setColor(new Color(255, 255, 255));
        g2d.setFont(Game.font64);
        g2d.drawString("How  to  play", 500, 320);
        g2d.setFont(Game.font24);
        g2d.drawString("Use  the  left  and  right  arrow  keys  to  move  left  and  right", 500, 420);
        g2d.drawString("q  to  rotate  counter  clockwise, W  to  rotate  180  degrees, E  to  rotate  clockwise", 500, 460);
        g2d.drawString("space  to  instantly  drop  a  piece,  down  arrow  to  drop  it  slowly,  shift  to  hold  the  piece", 500, 500);
        g2d.drawString("escape  to  leave  a  game,  tilde  key  to  restart  (top  left  of  keyboard)", 500, 540);
        g2d.drawString("is  is  highly  recommended  to  edit  the  handling_settings.txt  file  to  change  the  controls", 500, 580);
        g2d.drawString("for  an  average  player,  the  recommended  ARR  and  SDF  is  10-50,  DAS  100-300", 500, 620);
        g2d.drawString("ARR  and  SDF  can  be  set  to  zero  for  instant  movement", 500, 660);
        g2d.drawString("Read  the  readme.txt  file  for  more  information", 500, 700);

        // About
        g2d.setFont(Game.font64);
        g2d.drawString("About", 500, 820);
        g2d.setFont(Game.font32);
        g2d.drawString("Made  by  Aaron  Ye,  images  and  audio  are  from TETRIO.IO", 500, 880);
        g2d.drawString("Graphics  are  heavily  inspired  by  TETRIO.IO", 500, 920);
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
