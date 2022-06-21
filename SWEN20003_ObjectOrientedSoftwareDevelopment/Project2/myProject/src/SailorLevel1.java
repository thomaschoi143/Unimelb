import bagel.Input;
import bagel.util.Point;
import java.util.ArrayList;

/**
 * Class that inherits from Sailor and provides a template for the Sailor in Level1
 * SailorsLevel1 has pickedUpItems
 * @author Thomas Choi
 * @version 1.0
 */
public class SailorLevel1 extends Sailor {
    private final ArrayList<Item> pickedupItems = new ArrayList<Item>();

    /**
     * Method that constructs a SailorLevel1
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public SailorLevel1(double x, double y) {
        super(x, y);
    }

    /**
     * Method that performs state update particularly for SailorLevel1
     * Render the inventory and perform like the sailor in level0
     * @param input The Input of the user
     * @param nonOverlapables ArrayList of NonOverlapablesObjs
     * @param enemies ArrayList of Enemies
     * @param topLeftBound The Point of the top left bound
     * @param bottomRightBound The Point of the bottom right bound
     */
    public void update(Input input, ArrayList<NonOverlapable> nonOverlapables, ArrayList<Enemy> enemies, Point topLeftBound,
                       Point bottomRightBound) {
        renderInventory();
        super.update(input, nonOverlapables, enemies, topLeftBound, bottomRightBound);
    }

    // Method that renders the picked up items in the inventory in order
    private void renderInventory() {
        for (int i = 0; i < pickedupItems.size(); i++) {
            pickedupItems.get(i).drawInventoryIcon(i);
        }
    }


    /**
     * Method that gets pickedupItems
     * @return ArrayList<Item> This returns pickedupItems
     */
    public ArrayList<Item> getPickedupItems() {
        return pickedupItems;
    }
}
