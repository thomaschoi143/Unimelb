import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Sailor{
    private final static Image SAILOR_LEFT = new Image("res/sailorLeft.png");
    private final static Image SAILOR_RIGHT = new Image("res/sailorRight.png");
    private final static int MOVE_SIZE = 20;
    private final static int MAX_HEALTH_POINTS = 100;
    private final static int DAMAGE_POINTS = 25;

    private final static int WIN_X = 990;
    private final static int WIN_Y = 630;
    private final static int BOTTOM_EDGE = 670;
    private final static int TOP_EDGE = 60;

    private final static int HEALTH_X = 10;
    private final static int HEALTH_Y = 25;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 30;
    private final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    private int healthPoints;
    private int oldX;
    private int oldY;
    private int x;
    private int y;
    private Image currentImage;

    public Sailor(int startX, int startY){
        this.x = startX;
        this.y = startY;
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = SAILOR_RIGHT;
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Method that performs state update
     */
    public void update(Input input, Block[] blocks){
        // store old coordinates every time the sailor moves
        if (input.wasPressed(Keys.UP)){
            setOldPoints();
            move(0, -MOVE_SIZE);
        }else if (input.wasPressed(Keys.DOWN)){
            setOldPoints();
            move(0, MOVE_SIZE);
        }else if (input.wasPressed(Keys.LEFT)){
            setOldPoints();
            move(-MOVE_SIZE,0);
            currentImage = SAILOR_LEFT;
        }else if (input.wasPressed(Keys.RIGHT)){
            setOldPoints();
            move(MOVE_SIZE,0);
            currentImage = SAILOR_RIGHT;
        }
        currentImage.draw(x, y);
        checkCollisions(blocks);
        renderHealthPoints();
    }

    /**
     * Method that checks for collisions between sailor and blocks
     */
    private void checkCollisions(Block[] blocks){
        // check collisions and print log
        Rectangle sailorBox = currentImage.getBoundingBoxAt(new Point(x, y));
        for (Block current : blocks) {
            Rectangle blockBox = current.getBoundingBox();
            if (sailorBox.intersects(blockBox)) {
                healthPoints = healthPoints - current.getDAMAGE_POINTS();
                System.out.println("Block inflicts " + current.getDAMAGE_POINTS() + " damage points on Sailor. " +
                        "Sailor's current health: " + healthPoints + "/" + MAX_HEALTH_POINTS);
                moveBack();
            }
        }
    }

    /**
     * Method that moves the sailor given the direction
     */
    private void move(int xMove, int yMove){
        x += xMove;
        y += yMove;
    }

    /**
     * Method that stores the old coordinates of the sailor
     */
    private void setOldPoints(){
        oldX = x;
        oldY = y;
    }

    /**
     * Method that moves the sailor back to its previous position
     */
    private void moveBack(){
        x = oldX;
        y = oldY;
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    private void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, COLOUR);
    }

    /**
     * Method that checks if sailor's health is <= 0
     */
    public boolean isDead(){
        return healthPoints <= 0;
    }

    /**
     * Method that checks if sailor has reached the ladder
     */
    public boolean hasWon(){
        return (x >= WIN_X) && (y > WIN_Y);
    }

    /**
     * Method that checks if sailor has gone out-of-bound
     */
    public boolean isOutOfBound(){
        return (y > BOTTOM_EDGE) || (y < TOP_EDGE) || (x < 0) ||
                (x > Window.getWidth());
    }
}