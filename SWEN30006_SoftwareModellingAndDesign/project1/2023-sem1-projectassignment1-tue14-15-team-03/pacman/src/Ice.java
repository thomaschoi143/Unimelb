/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import java.awt.*;

public class Ice extends Item {
    private final static Color color = Color.blue;
    private final static String filename = "ice.png";
    private final static int earningScore = 0;
    public Ice() {
        super(filename, color, earningScore);
    }
}
