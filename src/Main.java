import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static JFrame frame;
    public static Game game;
    public static boolean soundOn = true;
    public static boolean useFlatSkin = false;

    // Calm or battle
    public static String musicType = "calm";

    public static void main(String[] args) throws LineUnavailableException, IOException {
        // Read the settings
        File settingsFile = new File("config.txt");
        Scanner sc = new Scanner(settingsFile);
        soundOn = sc.nextBoolean();
        useFlatSkin = sc.nextBoolean();
        sc.close();

        Map<String, String> handlingSettings = Utils.parseKeyValueFile("handling_settings.txt");
        TetrisBoard.arr = Integer.parseInt(handlingSettings.get("arr"));
        TetrisBoard.das = Integer.parseInt(handlingSettings.get("das"));
        TetrisBoard.sdf = Integer.parseInt(handlingSettings.get("sdf"));
        musicType = handlingSettings.get("music_type");
        if (!musicType.equals("calm") && !musicType.equals("battle")) {
            musicType = "calm";
        }

        frame = new JFrame("Tetris");

        game = new Game();

        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("assets/icons/tetrio.png"));
        frame.add(game);
        frame.addKeyListener(game);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game.switchState(Game.GameStates.MAIN_MENU);

        frame.setVisible(true);
    }
}
