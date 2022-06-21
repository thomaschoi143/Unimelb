import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Abstract class that provides a template for an item (Elixir, Potion, Sword)
 * @author Thomas Choi
 * @version 1.0
 */
public abstract class Item extends GameEntity{
    private final static int INVENTORY_OFFSET = 40;
    private final static Point INVENTORY_POINT = new Point(25, 70);
    private final Image ICON;

    /**
     * Method that constructs an Item
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     * @param name The name(type) of the item
     */
    public Item(double x, double y, String name) {
        super(x, y);
        this.ICON = new Image(String.format("res/items/%sIcon.png", name));
        setCurrentImage(new Image(String.format("res/items/%s.png", name)));
    }

    /**
     * Method that performs state update
     * @param sailor The sailor of level1
     * @return boolean This returns whether the item is still "alive"
     */
    public boolean update(SailorLevel1 sailor) {
        Rectangle sailorRec = new Rectangle(sailor.getX(), sailor.getY(), sailor.getCurrentImage().getWidth(),
                sailor.getCurrentImage().getHeight());
        Rectangle itemRec = new Rectangle(getX(), getY(), getCurrentImage().getWidth(), getCurrentImage().getHeight());

        if (itemRec.intersects(sailorRec)) {
            pickup(sailor);
            sailor.getPickedupItems().add(this);
            return false;
        }

        render();
        return true;
    }

    /**
     * Method that draws the item icon in the inventory in order
     * @param order The order that the sailor picked it up
     */
    public void drawInventoryIcon(int order) {
        ICON.draw(INVENTORY_POINT.x, INVENTORY_POINT.y + order * INVENTORY_OFFSET);
    }

    // Abstract method
    protected abstract void pickup(SailorLevel1 sailor);
}
