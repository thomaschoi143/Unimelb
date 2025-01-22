/* EraserMedium.java
   Author: Thomas Choi 1202247 */
package shared.whiteboard.drawElements;

public class EraserMedium extends Eraser {
    public static final int DIAMETER = 45;
    public EraserMedium(MyPoint center) {
        super(center, DIAMETER);
    }
}
