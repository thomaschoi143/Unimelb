/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.item;

import java.awt.*;

public class GoldBuilder extends ItemBuilder {
    private static GoldBuilder instance;
    private final static Color color = Color.yellow;
    private final static String filename = "sprites/gold.png";
    private final static int earningScore = 5;
    private final static ItemType type = ItemType.GOLD;

    private GoldBuilder() {}
    public static GoldBuilder getInstance() {
        if (instance == null) {
            instance = new GoldBuilder();
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
