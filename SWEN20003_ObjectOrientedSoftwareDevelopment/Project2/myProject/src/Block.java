import bagel.*;

/**
 * Class that inherits from GameEntity to provide a template for Block
 * Blocks are NonOverlapable
 * @author Thomas Choi
 * @version 1.0
 */
public class Block extends GameEntity implements NonOverlapable {
    private final static String FILENAME = "res/block.png";

    /**
     * Method that constructs a Block
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public Block(double x, double y) {
        super(x, y);
        setCurrentImage(new Image(FILENAME));
    }

}
