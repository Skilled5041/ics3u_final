// Aaron Ye
// 2023-06-18
// Reusable components
// Custom components that fit the design of the game

import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Components {
    static class MenuButton extends JButton {

        public enum ButtonTypes {
            NORMAL, BACK
        }

        public Clip backSoundClip = Utils.getAudio("assets/mm_sfx/mm_btn_back.wav");
        public Clip buttonClickClip = Utils.getAudio("assets/mm_sfx/mm_click_1.wav");
        public Clip buttonHoverClip = Utils.getAudio("assets/mm_sfx/mm_hover.wav");

        private Image icon = null;
        private Color background;
        private Color highlight;
        private Color shadow;
        private Color mainTextColor;
        private Color descriptionTextColor;
        private String mainText;
        private String descriptionText;
        ButtonTypes buttonType = ButtonTypes.NORMAL;

        private int buttonWidth = 1920 - 500;

        // Default width of the button
        private final int BASE_BUTTON_WIDTH = 1920 - 500;
        private final int BUTTON_HEIGHT = 200;

        // Builder methods

        /**
         * Sets the width of the button
         *
         * @param buttonType The type of button
         * @return The button
         */
        public MenuButton setType(ButtonTypes buttonType) {
            this.buttonType = buttonType;
            return this;
        }

        /**
         * Sets the icon of the button
         *
         * @param icon The icon of the button
         * @return The button
         */
        public MenuButton setIcon(Image icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the background color of the button
         *
         * @param background The background color of the button
         * @return The button
         */
        public MenuButton setBackgroundColor(Color background) {
            this.background = background;
            return this;
        }

        /**
         * Sets the highlight color of the button
         *
         * @param highlight The highlight color of the button
         * @return The button
         */
        public MenuButton setHighlightColor(Color highlight) {
            this.highlight = highlight;
            return this;
        }

        /**
         * Sets the shadow color of the button
         *
         * @param shadow The shadow color of the button
         * @return The button
         */
        public MenuButton setShadowColor(Color shadow) {
            this.shadow = shadow;
            return this;
        }


        /**
         * Sets the main text color of the button
         *
         * @param mainTextColor The main text color of the button
         * @return The button
         */
        public MenuButton setMainTextColor(Color mainTextColor) {
            this.mainTextColor = mainTextColor;
            return this;
        }

        /**
         * Sets the description text color of the button
         *
         * @param descriptionTextColor The description text color of the button
         * @return The button
         */
        public MenuButton setDescriptionTextColor(Color descriptionTextColor) {
            this.descriptionTextColor = descriptionTextColor;
            return this;
        }

        /**
         * Sets the main text of the button
         *
         * @param mainText The main text of the button
         * @return The button
         */
        public MenuButton setMainText(String mainText) {
            this.mainText = mainText;
            return this;
        }

        /**
         * Sets the description text of the button
         *
         * @param descriptionText The description text of the button
         * @return The button
         */
        public MenuButton setDescriptionText(String descriptionText) {
            this.descriptionText = descriptionText;
            return this;
        }

        enum MovementDirection {
            LEFT, RIGHT, NONE
        }

        // Animation variables
        private int xOffset = 0;
        private int iterCount = 0;
        private final int STEP = 4;

        MovementDirection direction = MovementDirection.NONE;

        public MenuButton() {
            super();

            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setPreferredSize(new Dimension(buttonWidth, BUTTON_HEIGHT));

            int darkerAmount = 10;

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    direction = MovementDirection.LEFT;

                    // Lighten the button on hover
                    setBackgroundColor(Utils.changeDarkness(background, darkerAmount));
                    setHighlightColor(Utils.changeDarkness(highlight, darkerAmount));
                    setShadowColor(Utils.changeDarkness(shadow, darkerAmount));
                    setMainTextColor(Utils.changeDarkness(mainTextColor, darkerAmount));
                    setDescriptionTextColor(Utils.changeDarkness(descriptionTextColor, darkerAmount));

                    // Play the hover sound
                    buttonHoverClip.setFramePosition(0);
                    Utils.playClip(buttonHoverClip);

                    // Move the button to the left
                    Timer timer = new Timer(4, null);
                    timer.addActionListener(e1 -> {
                        if (direction == MovementDirection.LEFT && iterCount < 100) {
                            float interpolation = Utils.easeOutCubic((float) iterCount / 100);

                            xOffset = (int) (-75 * interpolation);
                            buttonWidth = BASE_BUTTON_WIDTH - xOffset;
                            repaint();
                            revalidate();
                            iterCount += STEP;
                        } else {
                            timer.stop();
                        }
                    });
                    timer.start();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    direction = MovementDirection.RIGHT;

                    // Darken the button on exit
                    setBackgroundColor(Utils.changeDarkness(background, -darkerAmount));
                    setHighlightColor(Utils.changeDarkness(highlight, -darkerAmount));
                    setShadowColor(Utils.changeDarkness(shadow, -darkerAmount));
                    setMainTextColor(Utils.changeDarkness(mainTextColor, -darkerAmount));
                    setDescriptionTextColor(Utils.changeDarkness(descriptionTextColor, -darkerAmount));

                    // Move the button to the right
                    Timer timer = new Timer(4, null);
                    timer.addActionListener(e1 -> {
                        if (iterCount > 0) {
                            float interpolation = Utils.easeOutCubic((float) iterCount / 100);

                            xOffset = (int) (75 * interpolation);
                            buttonWidth = BASE_BUTTON_WIDTH + xOffset;
                            repaint();
                            revalidate();
                            iterCount -= STEP;
                        } else {
                            timer.stop();
                        }
                    });

                    timer.start();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // Play sound on click
                    if (buttonType == ButtonTypes.NORMAL) {
                        buttonClickClip.setFramePosition(0);
                        Utils.playClip(buttonClickClip);
                    } else if (buttonType == ButtonTypes.BACK) {
                        backSoundClip.setFramePosition(0);
                        Utils.playClip(backSoundClip);
                    }
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(buttonWidth, BUTTON_HEIGHT);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(buttonWidth, BUTTON_HEIGHT);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(buttonWidth, BUTTON_HEIGHT);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Background
            g2d.setColor(background);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Edge highlights
            g2d.setStroke(new BasicStroke(8));
            g2d.setColor(highlight);
            // Top
            g2d.drawLine(0, 0, getWidth(), 0);
            // Left
            g2d.drawLine(0, 0, 0, getHeight());
            // Bottom shadow
            g2d.setColor(shadow);
            g2d.drawLine(0, getHeight(), getWidth(), getHeight());
            // Right shadow
            g2d.drawLine(getWidth(), 0, getWidth(), getHeight());

            // Icon
            if (icon != null) {
                g2d.drawImage(icon, 50, 20, null);
            }
            // Text
            g2d.setColor(mainTextColor);
            g2d.setFont(Game.font64);
            g2d.drawString(mainText, 500, 80);
            g2d.setColor(descriptionTextColor);
            g2d.setFont(Game.font32);
            g2d.drawString(descriptionText, 500, 130);

            g2d.dispose();
        }
    }

    static class ToggleSwitch extends JToggleButton {
        private Color highlightColor;
        private Color shadowColor;
        private Color backgroundColor;
        private Color textColor;
        private String enabledText;
        private String disabledText;
        private boolean on = false;

        public Clip buttonClickClip = Utils.getAudio("assets/mm_sfx/mm_click_1.wav");
        public Clip buttonHoverClip = Utils.getAudio("assets/mm_sfx/mm_hover.wav");

        /**
         * Sets the highlight color of the toggle switch
         *
         * @param highlightColor The color of the highlight
         * @return The toggle switch
         */
        public ToggleSwitch setHighlightColor(Color highlightColor) {
            this.highlightColor = highlightColor;
            return this;
        }

        /**
         * Sets the shadow color of the toggle switch
         *
         * @param shadowColor The color of the shadow
         * @return The toggle switch
         */
        public ToggleSwitch setShadowColor(Color shadowColor) {
            this.shadowColor = shadowColor;
            return this;
        }

        /**
         * Sets the background color of the toggle switch
         *
         * @param backgroundColor The color of the background
         * @return The toggle switch
         */
        public ToggleSwitch setBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Sets the text color of the toggle switch
         *
         * @param textColor The color of the text
         * @return The toggle switch
         */
        public ToggleSwitch setTextColor(Color textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * Sets the text of the toggle switch when it is enabled
         *
         * @param enabledText The text to display when the toggle switch is enabled
         * @return The toggle switch
         */
        public ToggleSwitch setEnabledText(String enabledText) {
            this.enabledText = enabledText;
            return this;
        }

        /**
         * Sets the text of the toggle switch when it is disabled
         *
         * @param disabledText The text to display when the toggle switch is disabled
         * @return The toggle switch
         */
        public ToggleSwitch setDisabledText(String disabledText) {
            this.disabledText = disabledText;
            return this;
        }

        /**
         * Sets the state of the toggle switch
         *
         * @param state The state of the toggle switch
         * @return The toggle switch
         */
        public ToggleSwitch setState(boolean state) {
            on = state;
            return this;
        }

        /**
         * Gets the state of the toggle switch
         *
         * @return The state of the toggle switch
         */
        public boolean getState() {
            return on;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(100, 100);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(100, 100);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(100, 100);
        }

        public ToggleSwitch() {
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setPreferredSize(new Dimension(100, 100));
            setMinimumSize(new Dimension(100, 100));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Play hover sound
                    buttonHoverClip.setFramePosition(0);
                    Utils.playClip(buttonHoverClip);

                    // Make button lighter
                    backgroundColor = Utils.changeDarkness(backgroundColor, 10);
                    highlightColor = Utils.changeDarkness(highlightColor, 10);
                    shadowColor = Utils.changeDarkness(shadowColor, 10);
                    textColor = Utils.changeDarkness(textColor, 10);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // Make button darker
                    backgroundColor = Utils.changeDarkness(backgroundColor, -10);
                    highlightColor = Utils.changeDarkness(highlightColor, -10);
                    shadowColor = Utils.changeDarkness(shadowColor, -10);
                    textColor = Utils.changeDarkness(textColor, -10);
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // Play click sound, and toggle state
                    buttonClickClip.setFramePosition(0);
                    Utils.playClip(buttonClickClip);
                    on = !on;
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Background
            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Edge highlights
            g2d.setStroke(new BasicStroke(8));
            g2d.setColor(highlightColor);
            // Top
            g2d.drawLine(0, 0, getWidth(), 0);
            // Left
            g2d.drawLine(0, 0, 0, getHeight());
            // Bottom shadow
            g2d.setColor(shadowColor);
            g2d.drawLine(0, getHeight(), getWidth(), getHeight());
            // Right shadow
            g2d.drawLine(getWidth(), 0, getWidth(), getHeight());

            // Text
            g2d.setColor(textColor);
            g2d.setFont(Game.font32);
            if (on) {
                g2d.drawString(enabledText, 25, 60);
            } else {
                g2d.drawString(disabledText, 25, 60);
            }

            g2d.dispose();
        }
    }
}
