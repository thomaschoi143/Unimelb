import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Block{
    private final static Image BLOCK = new Image("res/block.png");
    private final static int DAMAGE_POINTS = 10;
    private final int x;
    private final int y;

    public Block(int startX, int startY){
        this.x = startX;
        this.y = startY;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        BLOCK.draw(x, y);
    }

    public int getDAMAGE_POINTS() {
        return DAMAGE_POINTS;
    }

    public Rectangle getBoundingBox(){
        return BLOCK.getBoundingBoxAt(new Point(x, y));
    }
}