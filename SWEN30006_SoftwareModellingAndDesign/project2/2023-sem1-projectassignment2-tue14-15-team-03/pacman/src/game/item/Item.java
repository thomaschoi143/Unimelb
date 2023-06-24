/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.item;

import ch.aplu.jgamegrid.*;

import java.awt.*;

public class Item extends Actor {
    private Color color;
    private int earningScore;
    private ItemType type;
    // For Pill
    public Item () {
        super();
    }

    // For Golds and Ice Cubes
    public Item (String filename) {
        super(filename);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setEarningScore(int earningScore) {
        this.earningScore = earningScore;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public int getEarningScore() {
        return earningScore;
    }

    public ItemType getType() {
        return type;
    }
}
