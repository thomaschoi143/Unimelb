/* DrawElement.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

public abstract class DrawElement implements Serializable {
    private Color color;
    @Serial
    private static final long serialVersionUID = 12345671231320L;

    public DrawElement(Color color) {
        this.color = color;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
    };

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
