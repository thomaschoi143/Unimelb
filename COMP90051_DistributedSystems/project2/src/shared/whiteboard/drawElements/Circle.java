/* Circle.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

import shared.Util;

import java.awt.*;

public class Circle extends Oval{
    public Circle(Color color, MyPoint start, MyPoint end) {
        super(color,
                Util.getUpperLeft(start, end),
                Math.abs(start.getX() - end.getX()));
    }

    public Circle(Color color, MyPoint topLeft, int diameter) {
        super(color, topLeft, diameter);
    }
}
