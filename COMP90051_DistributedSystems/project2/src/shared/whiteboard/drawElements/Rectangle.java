/* Rectangle.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

import shared.Util;

import java.awt.*;

public class Rectangle extends DrawElement{
    private MyPoint topLeft;
    private int width;
    private int height;

    public Rectangle(Color color, MyPoint start, MyPoint end) {
        super(color);
        this.topLeft = Util.getUpperLeft(start, end);
        width = Math.abs(end.getX() - start.getX());
        height = Math.abs(end.getY() - start.getY());
    }

    public Rectangle(Color color, MyPoint topLeft, int width, int height) {
        super(color);
        this.topLeft = topLeft;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics2D g) {
        super.draw(g);
        g.drawRect(topLeft.getX(), topLeft.getY(), width, height);
    }
}
