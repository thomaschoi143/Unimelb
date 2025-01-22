/* Line.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

import java.awt.*;

public class Line extends DrawElement {
    private MyPoint p1;
    private MyPoint p2;

    public Line(Color color, MyPoint p1, MyPoint p2) {
        super(color);
        this.p1 = p1;
        this.p2 = p2;
    }

    public void draw(Graphics2D g) {
        super.draw(g);
        g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
}
