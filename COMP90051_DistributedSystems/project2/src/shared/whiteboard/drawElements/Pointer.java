/* Pointer.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

import shared.whiteboard.ActionCode;

import java.awt.*;

public class Pointer extends DrawElement {
    private MyPoint pt;
    private boolean isInBoard = false;
    private int diameter = DEFAULT_DIAMETER;
    private static final int DEFAULT_DIAMETER = 6;

    public Pointer(Color color, MyPoint pt) {
        super(color);
        this.pt = pt;
    }

    public void update(int x, int y, ActionCode actionCode) {
        this.pt = new MyPoint(Math.max(0, x - diameter/2), Math.max(0, y - diameter/2));
        if (actionCode == ActionCode.ERASE_S) {
            diameter = EraserSmall.DIAMETER;
        } else if (actionCode == ActionCode.ERASE_M) {
            diameter = EraserMedium.DIAMETER;
        } else if (actionCode == ActionCode.ERASE_L) {
            diameter = EraserLarge.DIAMETER;
        } else {
            diameter = DEFAULT_DIAMETER;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        g.drawOval(pt.getX(), pt.getY(), diameter, diameter);
    }

    public boolean isInBoard() {
        return isInBoard;
    }

    public void setInBoard(boolean inBoard) {
        isInBoard = inBoard;
    }
}
