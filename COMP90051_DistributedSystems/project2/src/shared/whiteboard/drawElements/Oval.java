/* Oval.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

import shared.Util;

import java.awt.*;

public class Oval extends DrawElement{
    private MyPoint topLeft;
    private int diameterX, diameterY;

    public Oval(Color color, MyPoint start, MyPoint end) {
        super(color);
        topLeft = Util.getUpperLeft(start, end);
        diameterX = Math.abs(end.getX() - start.getX());
        diameterY = Math.abs(end.getY() - start.getY());
    }

    public Oval(Color color, MyPoint topLeft, int diameter) {
        super(color);
        this.topLeft = topLeft;
        this.diameterX = diameter;
        this.diameterY = diameter;
    }

    public Oval(Color color, MyPoint topLeft, int diameterX, int diameterY) {
        super(color);
        this.topLeft = topLeft;
        this.diameterX = diameterX;
        this.diameterY = diameterY;
    }

    public void draw(Graphics2D g) {
        super.draw(g);
        g.drawOval(topLeft.getX(), topLeft.getY(), diameterX, diameterY);
    }
}
