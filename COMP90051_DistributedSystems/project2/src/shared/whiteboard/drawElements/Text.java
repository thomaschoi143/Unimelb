/* Text.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard.drawElements;

import java.awt.*;

public class Text extends DrawElement {
    private MyPoint center;
    private String text;
    private final static int FONTSIZE = 18;

    public Text(Color color, MyPoint center, String text) {
        super(color);
        this.center = center;
        this.text = text;
    }

    public void draw(Graphics2D g) {
        super.draw(g);
        g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, FONTSIZE));
        g.drawString(text, center.getX(), center.getY());
    }
}
