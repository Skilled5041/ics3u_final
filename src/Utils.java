// Aaron Ye
// 2023-06-18
// Utility methods
// Various methods and classes used throughout the game

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Utils {
    // An object that holds two values
    static class Pair<T1, T2> {
        public T1 first;
        public T2 second;

        public Pair(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "Utils.Pair(" + first.toString() + ", " + second.toString() + ")";
        }

        @Override
        public int hashCode() {
            return first.hashCode() ^ second.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair<?, ?> pair)) {
                return false;
            }
            return this.first.equals(pair.first) && this.second.equals(pair.second);
        }
    }

    /**
     * Wraps the audio code in a method for conciseness
     *
     * @param path Path to the audio file
     * @return Clip object with the audio opened
     */
    public static Clip getAudio(String path) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(path)));
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to load audio file: " + path);
    }

    /**
     * Returns a random integer between min and max, inclusive.
     *
     * @param min Minimum value
     * @param max Maximum value
     * @return Random integer between min and max, inclusive
     */
    public static int randomInteger(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    /**
     * Transforms a colour
     *
     * @param color  The colour to transform
     * @param amount a fixed amount that is added / subtracted from each RGB value
     * @return The transformed colour
     */
    public static Color changeDarkness(Color color, int amount) {
        return new Color(Math.max(0, Math.min(255, color.getRed() + amount)),
                Math.max(0, Math.min(255, color.getGreen() + amount)),
                Math.max(0, Math.min(255, color.getBlue() + amount)));
    }

    // Credits: https://easings.net/
    // Various easing functions for animations

    /**
     * Easing function for animations
     *
     * @param x Input value (0-1)
     * @return Output value (0-1)
     */
    public static float easeOutCubic(float x) {
        return x < 0.5 ? 4 * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 3) / 2);
    }

    /**
     * Easing function for animations
     *
     * @param x Input value (0-1)
     * @return Output value (0-1)
     */
    public static float easeInOutElastic(float x) {
        float c5 = (float) ((2 * Math.PI) / 4.5);
        return x == 0
                ? 0
                : (float) (x == 1
                ? 1
                : x < 0.5
                ? -(Math.pow(2, 20 * x - 10) * Math.sin((20 * x - 11.125) * c5)) / 2
                : (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1);
    }

    /**
     * Easing function for animations
     *
     * @param x Input value (0-1)
     * @return Output value (0-1)
     */
    public static float easeInElastic(float x) {
        float c4 = (float) ((2 * Math.PI) / 3);

        return x == 0
                ? 0
                : (float) (x == 1
                ? 1
                : -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4));
    }

    /**
     * Easing function for animations
     *
     * @param x Input value (0-1)
     * @return Output value (0-1)
     */
    public static float easeOutElastic(float x) {
        float c4 = (float) ((2 * Math.PI) / 3);

        return x == 0
                ? 0
                : (float) (x == 1
                ? 1
                : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1);
    }

    /**
     * Easing function for animations
     *
     * @param x Input value (0-1)
     * @return Output value (0-1)
     */
    public static float easeOutBack(float x) {
        float c1 = 1.70158F;
        float c3 = c1 + 1;

        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    /**
     * Easing function for animations
     *
     * @param x Input value (0-1)
     * @return Output value (0-1)
     */
    public static float easeOutExpo(float x) {
        return x == 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
    }

    /**
     * Draws an image centered at a position
     *
     * @param g2d    Graphics2D object
     * @param image  Image to draw
     * @param posX   X position
     * @param posY   Y position
     * @param width  Width
     * @param height Height
     */
    public static void drawCenteredImage(Graphics2D g2d, Image image, int posX, int posY, int width, int height) {
        g2d.drawImage(image, posX - width / 2, posY - height / 2, width, height, null);
    }

    /**
     * Loops a clip if sound is on
     *
     * @param clip Clip to loop
     */
    public static void loopClip(Clip clip) {
        if (!Main.soundOn) return;
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Plays a clip if sound is on
     *
     * @param clip Clip to play
     */
    public static void playClip(Clip clip) {
        if (!Main.soundOn) return;
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * Parses a file that contains key-value pairs (separated by =, one per line)
     *
     * @param filePath Path to the file
     * @return Map of key-value pairs
     */
    public static Map<String, String> parseKeyValueFile(String filePath) {
        Map<String, String> map = new HashMap<>();
        File file = new File(filePath);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("=");
                if (split.length != 2) continue;
                map.put(split[0], split[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Gets a random audio file from a folder
     *
     * @param folderPath Path to the folder
     * @return Clip object with the audio
     */
    public static Clip randomAudio(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files == null) return null;
        File file = files[randomInteger(0, files.length - 1)];
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the volume of a clip
     * @param clip Clip to set the volume of
     * @param percent Volume (0-1)
     * @return Clip with the volume set
     */
    public static Clip setVolume(Clip clip, float percent) {
        percent = Math.max(0, Math.min(1, percent));
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log(percent) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
        return clip;
    }
}
