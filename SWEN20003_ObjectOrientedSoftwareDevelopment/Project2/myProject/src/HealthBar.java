import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

/**
 * Class that provides a template for a health bar
 * @author Thomas Choi
 * @version 1.0
 */
public class HealthBar {
    private final static String FILENAME = "res/wheaton.otf";
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;

    /**
     * Method that draws the health bar according to the font size, location and game entity's health points
     * @param size The font size of the health bar
     * @param point The bottom left Point of the health bar
     * @param maxPoints The max health points of the game entity
     * @param currentPoints The current health points of the game entity
     */
    public void draw(int size, Point point, int maxPoints, int currentPoints) {
        Font HEALTH_POINTS = new Font(FILENAME, size);
        double percentage = (double) currentPoints / maxPoints * 100;
        Colour colour = GREEN;

        if (percentage < RED_BOUNDARY) {
            colour = RED;
        } else if (percentage < ORANGE_BOUNDARY) {
            colour = ORANGE;
        }

        HEALTH_POINTS.drawString(String.format("%.0f%%", percentage), point.x, point.y,
                new DrawOptions().setBlendColour(colour));
    }
}
