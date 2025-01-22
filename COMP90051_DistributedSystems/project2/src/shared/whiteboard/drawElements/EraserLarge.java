/* EraserLarge.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

public class EraserLarge extends Eraser {
    public static final int DIAMETER = 60;
    public EraserLarge(MyPoint center) {
        super(center, DIAMETER);
    }
}
