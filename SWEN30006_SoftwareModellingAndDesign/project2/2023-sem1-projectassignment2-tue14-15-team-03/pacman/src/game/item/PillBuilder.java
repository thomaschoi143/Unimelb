/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.item;

import java.awt.*;

public class PillBuilder extends ItemBuilder {
    private static PillBuilder instance;
    private final static int earningScore = 1;
    private final static Color color = Color.white;
    private final static ItemType type = ItemType.PILL;

    private PillBuilder() {}

    public static PillBuilder getInstance() {
        if (instance == null) {
            instance = new PillBuilder();
        }
        return instance;
    }

    protected void setItemActor() {
        item = new Item();
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
