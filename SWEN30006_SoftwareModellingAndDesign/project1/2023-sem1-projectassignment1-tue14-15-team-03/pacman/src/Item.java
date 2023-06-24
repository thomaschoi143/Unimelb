/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src;

import ch.aplu.jgamegrid.*;

import java.awt.*;

public abstract class Item extends Actor {
    private final static String spritesFolder = "sprites/";
    private Color color;
    private int earningScore;

    // For Pills
    public Item (Color color, int earningScore) {
        super();
        setUpItem(color, earningScore);
    }

    // For Golds and Ice Cubes
    public Item (String filename, Color color, int earningScore) {
        super(spritesFolder + filename);
        setUpItem(color, earningScore);
    }

    private void setUpItem(Color color, int earningScore) {
        this.color = color;
        this.earningScore = earningScore;
    }

    public Color getColor() {
        return color;
    }

    public int getEarningScore() {
        return earningScore;
    }
}
