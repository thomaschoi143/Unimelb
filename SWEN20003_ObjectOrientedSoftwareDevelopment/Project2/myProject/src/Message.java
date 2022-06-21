import bagel.*;

/**
 * Class that provides a template for the instruction message
 * @author Thomas Choi
 * @version 1.0
 */
public class Message {
    private final static int SIZE = 55;
    private final static int Y = 402;

    private final Font MESSAGE = new Font("res/wheaton.otf", SIZE);

    /**
     * Method that draws the string with offset
     * @param str The string that needs to be drawn
     * @param offset The offset in y direction
     */
    public void drawMessage(String str, double offset) {
        MESSAGE.drawString(str, Window.getWidth()/2.0 - MESSAGE.getWidth(str)/2.0, Y + offset);
    }
}
