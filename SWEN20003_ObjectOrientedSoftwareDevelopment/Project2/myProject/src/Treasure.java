import bagel.*;

/**
 * Class that inherits from GameEntity and provides a template for Treasure
 * @author Thomas Choi
 * @version 1.0
 */
public class Treasure extends GameEntity{
    private final static String FILENAME = "res/treasure.png";

    /**
     * Method that constructs a Treasure
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public Treasure(double x, double y) {
        super(x, y);
        setCurrentImage(new Image(FILENAME));
    }
}
