import bagel.*;
import bagel.util.Rectangle;
import bagel.util.Point;

public class Block {
    private final static int DAMAGE_POINTS = 10;
    private final Image IMAGE = new Image("res/block.png");
    private final int x, y;

    // Constructor
    public Block(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Attack the sailor and reduce his health points
     */
    public int attack(Sailor sailor) {
        Rectangle blockRectangle = IMAGE.getBoundingBoxAt(new Point(x, y));
        Rectangle sailorRectangle = sailor.getImage().getBoundingBoxAt(new Point(sailor.getX(), sailor.getY()));

        if (blockRectangle.intersects(sailorRectangle)) {
            sailor.loseHealthPoints(DAMAGE_POINTS);
            System.out.format("Block inflicts %d damage points on Sailor. Sailor's current health: %d/%d\n",
                    DAMAGE_POINTS, sailor.getHealthPoints(), Sailor.MAX_HEALTH_POINTS);
            return 1;
        }
        return 0;
    }

    // Getters
    public Image getIMAGE() {
        return IMAGE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
