import bagel.*;

public class Sailor {
    private final static int DAMAGE_POINT = 25;
    private final static int STEP_SIZE = 20;
    public final static int MAX_HEALTH_POINTS = 100;

    private final Image LEFT_IMAGE = new Image("res/sailorLeft.png");
    private final Image RIGHT_IMAGE = new Image("res/sailorRight.png");

    private boolean isFacingRight = true;
    private int healthPoints = MAX_HEALTH_POINTS;
    private int x, y;

    // Constructor
    public Sailor(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Show the sailor image in the correct direction
     */
    public Image getImage() {
        if (isFacingRight) {
            return RIGHT_IMAGE;
        }
        return LEFT_IMAGE;
    }

    /**
     * Sailor moves by the input key
     */
    public void move(Input input) {
        if (input.wasPressed(Keys.RIGHT)) {
            isFacingRight = true;
            x += STEP_SIZE;
        }
        if (input.wasPressed(Keys.LEFT)) {
            isFacingRight = false;
            x -= STEP_SIZE;
        }
        if (input.wasPressed(Keys.UP)) {
            y -= STEP_SIZE;
        }
        if (input.wasPressed(Keys.DOWN)) {
            y += STEP_SIZE;
        }
    }

    /**
     * Sailor rebounced when attacked by a block
     */
    public void rebounce(Input input) {
        if (input.wasPressed(Keys.RIGHT)) {
            x -= STEP_SIZE;
        }
        if (input.wasPressed(Keys.LEFT)) {
            x += STEP_SIZE;
        }
        if (input.wasPressed(Keys.UP)) {
            y += STEP_SIZE;
        }
        if (input.wasPressed(Keys.DOWN)) {
            y -= STEP_SIZE;
        }
    }

    /**
     * Lose health points by the block's damage points
     */
    public void loseHealthPoints(int points) {
        healthPoints -= points;
    }

    // Getters
    public int getHealthPoints() {
        return healthPoints;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
