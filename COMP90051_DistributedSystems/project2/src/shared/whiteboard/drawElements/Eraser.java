/* Eraser.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

import java.awt.*;

public abstract class Eraser extends DrawElement{
    private MyPoint center;
    private final int diameter;

    private static final Color COLOR = Color.WHITE;
    public Eraser(MyPoint center, int diameter) {
        super(COLOR);
        this.center = center;
        this.diameter = diameter;
    }

    public void draw(Graphics2D g) {
        super.draw(g);
        g.fillOval(center.getX() - diameter/2, center.getY() - diameter/2, diameter, diameter);
    }
}
