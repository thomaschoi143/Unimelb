/* MyPoint.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

import java.io.Serial;
import java.io.Serializable;

public class MyPoint implements Serializable {
    private int x;
    private int y;
    @Serial
    private static final long serialVersionUID = 12345671231320L;

    public MyPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
