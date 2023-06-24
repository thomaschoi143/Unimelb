/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import java.awt.*;

public class Gold extends Item {
    private final static Color color = Color.yellow;
    private final static String filename = "gold.png";
    private final static int earningScore = 5;

    public Gold() {
        super(filename, color, earningScore);
    }


}
