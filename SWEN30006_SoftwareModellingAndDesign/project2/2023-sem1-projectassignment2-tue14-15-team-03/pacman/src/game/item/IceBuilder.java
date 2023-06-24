/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.item;

import java.awt.*;

public class IceBuilder extends ItemBuilder{
    private static IceBuilder instance;
    private final static Color color = Color.blue;
    private final static String filename = "sprites/ice.png";
    private final static int earningScore = 0;
    private final static ItemType type = ItemType.ICE;

    private IceBuilder() {}
    public static IceBuilder getInstance() {
        if (instance == null) {
            instance = new IceBuilder();
        }
        return instance;
    }
    protected void setItemActor() {
        item = new Item(filename);
    }
    protected void setColor() {
        item.setColor(color);
    }
    protected void setEarningScore() {
        item.setEarningScore(earningScore);
    }
    protected void setItemType() {
        item.setType(type);
    }
}
