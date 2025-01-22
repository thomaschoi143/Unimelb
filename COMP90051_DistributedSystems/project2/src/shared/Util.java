/* Util.java
   Author: Thomas Choi 1202247 */

package shared;

import shared.whiteboard.drawElements.MyPoint;

public class Util {
    public static MyPoint getUpperLeft(MyPoint pt1, MyPoint pt2) {
        return new MyPoint(Math.min(pt1.getX(), pt2.getX()), Math.min(pt1.getY(), pt2.getY()));
    }

    public static String getIdStr(int clientId) {
        return String.format("%04d", clientId);
    }
}
