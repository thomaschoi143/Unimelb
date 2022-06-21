import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.Random;

/**
 * Abstract class that provides a template for enemies (Pirate and Blackbeard)
 * Enemies are Rebounceable and Attackable
 * @author Thomas Choi
 * @version 1.0
 */
public abstract class Enemy extends GameEntity implements Rebounceable, Attackable {
    // Direction constants
    private final static int LEFT = 0;
    private final static int RIGHT = 1;
    private final static int UP = 2;
    private final static int NUM_DIRECTIONS = 4;
    private final static int NO_DIR = 0;
    private final static int INCREASING_COORDINATE = 1;
    private final static int DECREASING_COORDINATE = -1;

    // Speed bound constants
    private final static double SPEED_LOWER_BOUND = 0.2;
    private final static double SPEED_UPPER_BOUND = 0.7;

    // Health bar constants
    private final static int HEALTH_BAR_SIZE = 15;
    private final static int HEALTH_BAR_Y_OFFSET = 6;

    // Invincible time constants
    private final static int INVINCIBLE_MS = 1500;

    private final Image HIT_LEFT_IMAGE;
    private final Image HIT_RIGHT_IMAGE;
    private final Image RIGHT_IMAGE;
    private final Image LEFT_IMAGE;

    private final HealthBar HEALTHBAR = new HealthBar();
    private boolean isAttack = true;
    private boolean isInvincible = false;
    private final double SPEED;
    private int xDir = NO_DIR, yDir = NO_DIR;
    private int currentHealthPoints;

    // Time counter
    private int cooldownTimeCounter = 0;
    private int invincibleTimeCounter = 0;

    /**
     * Method that constructs an Enemy
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     * @param maxHealthPoints The max health points of the enemy
     * @param name The name(type) of the enemy
     */
    public Enemy (double x, double y, int maxHealthPoints, String name) {
        super(x, y);

        String filename = name.toLowerCase();
        HIT_LEFT_IMAGE = new Image(String.format("res/%1$s/%1$sHitLeft.png", filename));
        HIT_RIGHT_IMAGE = new Image(String.format("res/%1$s/%1$sHitRight.png", filename));
        LEFT_IMAGE = new Image(String.format("res/%1$s/%1$sLeft.png", filename));
        RIGHT_IMAGE = new Image(String.format("res/%1$s/%1$sRight.png", filename));

        Random ran = new Random();
        int randomNum = ran.nextInt(NUM_DIRECTIONS);

        setCurrentImage(RIGHT_IMAGE);
        if (randomNum == LEFT) {
            xDir = DECREASING_COORDINATE;
            setCurrentImage(LEFT_IMAGE);
        } else if (randomNum == RIGHT) {
            xDir = INCREASING_COORDINATE;
        } else if (randomNum == UP) {
            yDir = DECREASING_COORDINATE;
        } else {
            // Down direction
            yDir = INCREASING_COORDINATE;
        }

        // Random value between lower bound and upper bound
        SPEED = SPEED_LOWER_BOUND + (SPEED_UPPER_BOUND - SPEED_LOWER_BOUND) * ran.nextDouble();

        currentHealthPoints = maxHealthPoints;
    }

    /**
     * Method that performs state update
     * @param nonOverlapables The ArrayList of NonOverlapableObjs
     * @param sailor The sailor of the game level
     * @param projectiles The ArrayList of projectiles
     * @param topLeftBound The Point of the top left bound
     * @param bottomRightBound The Point of the bottom right bound
     * @return boolean This returns whether enemy is still alive
     */
    public boolean update(ArrayList<NonOverlapable> nonOverlapables, Sailor sailor, ArrayList<Projectile> projectiles
            , Point topLeftBound, Point bottomRightBound) {
        if (currentHealthPoints <= 0) {
            // The enemy has died
            return false;
        }

        if (!isAttack) {
            // The enemy is cooling down
            cooldownTimeCounter++;
            if (cooldownTimeCounter / (REFRESH_RATE / 1000.0) == getCooldownMs()) {
                isAttack = true;
                cooldownTimeCounter = 0;
            }
        }

        if (isInvincible) {
            checkInvincibleEnd();
        }

        move(xDir * SPEED, yDir * SPEED);
        render();
        HEALTHBAR.draw(HEALTH_BAR_SIZE, new Point(getX(), getY() - HEALTH_BAR_Y_OFFSET), getMaxHealthPoints(),
                currentHealthPoints);

        for (NonOverlapable nonOverlapable : nonOverlapables) {
            if (nonOverlapable.nonOverlap(this)) {
                break;
            }
        }

        if (outOfBound(topLeftBound, bottomRightBound)) {
            rebounce();
        }

        if (isAttack) {
            // Attack mode
            if (attack(sailor)) {
                projectiles.add(fireProjectile(new Point(sailor.getX(), sailor.getY())));
            }
        }

        return true;
    }

    /**
     * Method that reduces his health points by points
     * @param points The points that lose
     */
    public void loseHealthPoints(int points) {
        currentHealthPoints -= points;

        // Trigger invincible state
        isInvincible = true;
        if (getCurrentImage() == RIGHT_IMAGE) {
            setCurrentImage(HIT_RIGHT_IMAGE);
        } else if (getCurrentImage() == LEFT_IMAGE) {
            setCurrentImage(HIT_LEFT_IMAGE);
        }
    }

    /**
     * Method that performs the rebounce behaviour
     */
    @Override
    public void rebounce() {
        if (xDir == DECREASING_COORDINATE) {
            // initially moving to left
            if (isInvincible) {
                setCurrentImage(HIT_RIGHT_IMAGE);
            } else {
                setCurrentImage(RIGHT_IMAGE);
            }
        } else if (xDir == INCREASING_COORDINATE) {
            // initially moving to right
            if (isInvincible) {
                setCurrentImage(HIT_LEFT_IMAGE);
            } else {
                setCurrentImage(LEFT_IMAGE);
            }
        }

        xDir *= -1;
        yDir *= -1;
    }

    // Method that checks if invincible state has ended
    private void checkInvincibleEnd() {
        invincibleTimeCounter++;
        if (invincibleTimeCounter / (REFRESH_RATE / 1000.0) == INVINCIBLE_MS) {
            // end invincible state
            isInvincible = false;
            invincibleTimeCounter = 0;

            if (getCurrentImage() == HIT_LEFT_IMAGE) {
                setCurrentImage(LEFT_IMAGE);
            } else {
                setCurrentImage(RIGHT_IMAGE);
            }
        }
    }

    /**
     * Method that performs if the enemy can attack the sailor
     * @param target The target(sailor) to attack
     * @return boolean This returns whether the enemy can attack the target
     */
    @Override
    public boolean attack(GameEntity target) {
        // Have a check before downcasting
        if (target instanceof Sailor) {
            Sailor sailor = (Sailor)target;
            Rectangle sailorRec = new Rectangle(sailor.getX(), sailor.getY(), sailor.getCurrentImage().getWidth(),
                    sailor.getCurrentImage().getHeight());
            Rectangle attackRangeRec = new Rectangle(getCenterX() - getAttackRange() / 2.0,
                    getCenterY() - getAttackRange() / 2.0, getAttackRange(), getAttackRange());

            if (attackRangeRec.intersects(sailorRec)) {
                isAttack = false;
                return true;
            }
        }
        return false;
    }

    // Abstract methods
    protected abstract Projectile fireProjectile(Point sailor);
    protected abstract int getMaxHealthPoints();
    protected abstract int getAttackRange();
    protected abstract int getCooldownMs();
    protected abstract String getName();

    // Getters and setters
    /**
     * Method that gets isInvincible
     * @return boolean This returns isInvincible
     */
    public boolean isInvincible() {
        return isInvincible;
    }

    /**
     * Method that gets currentHealthPoints
     * @return int This returns currentHealthPoints
     */
    public int getCurrentHealthPoints() {
        return currentHealthPoints;
    }
}
