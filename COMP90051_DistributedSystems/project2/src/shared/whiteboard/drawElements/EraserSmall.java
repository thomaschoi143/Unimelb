/* EraserSmall.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

public class EraserSmall extends Eraser {
    public static final int DIAMETER = 30;
    public EraserSmall(MyPoint center) {
        super(center, DIAMETER);
    }
}
