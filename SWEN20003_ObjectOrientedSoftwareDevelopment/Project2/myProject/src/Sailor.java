import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;

/**
 * Class that inherits from GameEntity and provides a template for Sailor
 * Sailors are Attackable and Rebounceable
 * @author Thomas Choi
 * @version 1.0
 */
public class Sailor extends GameEntity implements Attackable, Rebounceable {
    private final static int SPEED = 1;
    private final static int DEFAULT_MAX_HEALTH_POINTS = 100;
    private final static int DEFAULT_DAMAGE_POINTS = 15;
    private final static int ATTACK_MS = 1000;
    private final static int COOLDOWN_MS = 2000;
    private final static int HEALTH_BAR_SIZE = 30;
    private final static Point HEALTH_BAR_POINT = new Point(10, 25);

    private final Image HIT_LEFT_IMAGE = new Image("res/sailor/sailorHitLeft.png");
    private final Image HIT_RIGHT_IMAGE = new Image("res/sailor/sailorHitRight.png");
    private final Image LEFT_IMAGE = new Image("res/sailor/sailorLeft.png");
    private final Image RIGHT_IMAGE = new Image("res/sailor/sailorRight.png");

    private final HealthBar HEALTHBAR = new HealthBar();
    private int healthPoints = DEFAULT_MAX_HEALTH_POINTS;
    private int currentMaxHealthPoints = DEFAULT_MAX_HEALTH_POINTS;
    private int currentDamagePoints = DEFAULT_DAMAGE_POINTS;
    private boolean isAttack = false;
    private int attackTimeCounter = 0;
    private int cooldownTimeCounter = 0;
    private boolean isCoolingDown = false;

    private double oldX;
    private double oldY;

    /**
     * Method that constructs a Sailor
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public Sailor(double x, double y) {
        super(x, y);
        setCurrentImage(RIGHT_IMAGE);
    }

    /**
     * Method that performs state update
     * @param input The Input of the user
     * @param nonOverlapables ArrayList of NonOverlapablesObjs
     * @param enemies ArrayList of Enemies
     * @param topLeftBound The Point of the top left bound
     * @param bottomRightBound The Point of the bottom right bound
     */
    public void update(Input input, ArrayList<NonOverlapable> nonOverlapables, ArrayList<Enemy> enemies, Point topLeftBound,
                       Point bottomRightBound) {
        if (!isCoolingDown && !isAttack && input.wasPressed(Keys.S)) {
            triggerAttack();
        }

        setOldPoints();
        if (input.isDown(Keys.RIGHT)) {
            if (isAttack) {
                setCurrentImage(HIT_RIGHT_IMAGE);
            } else {
                setCurrentImage(RIGHT_IMAGE);
            }
            move(SPEED, 0);
        } else if (input.isDown(Keys.LEFT)) {
            if (isAttack) {
                setCurrentImage(HIT_LEFT_IMAGE);
            } else {
                setCurrentImage(LEFT_IMAGE);
            }
            move(-SPEED, 0);
        } else if (input.isDown(Keys.UP)) {
            move(0, -SPEED);
        } else if (input.isDown(Keys.DOWN)) {
            move(0, SPEED);
        }

        if (isAttack) {
            checkAttackTime();
        } else if (isCoolingDown) {
            checkCooldownTime();
        }

        render();
        HEALTHBAR.draw(HEALTH_BAR_SIZE, HEALTH_BAR_POINT, currentMaxHealthPoints, healthPoints);

        for (NonOverlapable nonOverlapable : nonOverlapables) {
            nonOverlapable.nonOverlap(this);
        }

        if (outOfBound(topLeftBound, bottomRightBound)) {
            rebounce();
        }

        if (isAttack) {
            // Sailor is in attack mode
            for (Enemy enemy : enemies) {
                attack(enemy);
            }
        }
    }

    // Method that triggers the attack mode
    private void triggerAttack() {
        isAttack = true;
        if (getCurrentImage() == RIGHT_IMAGE) {
            setCurrentImage(HIT_RIGHT_IMAGE);
        } else {
            setCurrentImage(HIT_LEFT_IMAGE);
        }
    }

    // Method that checks if the attack period has ended
    private void checkAttackTime() {
        attackTimeCounter++;
        if (attackTimeCounter / (REFRESH_RATE / 1000.0) == ATTACK_MS) {
            isAttack = false;
            isCoolingDown = true;
            attackTimeCounter = 0;
            if (getCurrentImage() == HIT_RIGHT_IMAGE) {
                setCurrentImage(RIGHT_IMAGE);
            } else {
                setCurrentImage(LEFT_IMAGE);
            }
        }
    }

    // Method that checks if the cooldown period has ended
    private void checkCooldownTime() {
        cooldownTimeCounter++;
        if (cooldownTimeCounter / (REFRESH_RATE / 1000.0) == COOLDOWN_MS) {
            isCoolingDown = false;
            cooldownTimeCounter = 0;
        }
    }

    // Method that stores the old coordinates of the sailor
    private void setOldPoints(){
        oldX = getX();
        oldY = getY();
    }

    // Method that performs the rebounce movement of the sailor
    @Override
    public void rebounce() {
        setX(oldX);
        setY(oldY);
    }

    /**
     * Method that determines if the sailor can attack the enemy
     * @param target The target to attack
     * @return boolean This returns whether the sailor has attacked the target
     */
    @Override
    public boolean attack(GameEntity target) {
        // Have a check before downcasting
        if (target instanceof Enemy) {
            Enemy enemy = (Enemy)target;
            Rectangle sailorRec = new Rectangle(getX(), getY(), getCurrentImage().getWidth(), getCurrentImage().getHeight());
            Rectangle enemyRec = new Rectangle(enemy.getX(), enemy.getY(), enemy.getCurrentImage().getWidth(),
                    enemy.getCurrentImage().getHeight());

            if (sailorRec.intersects(enemyRec) && !enemy.isInvincible()) {
                enemy.loseHealthPoints(currentDamagePoints);
                System.out.format("Sailor inflicts %d damage points on %s. %2$s's current health: %d/%d\n",
                        currentDamagePoints, enemy.getName(), enemy.getCurrentHealthPoints(), enemy.getMaxHealthPoints());
                return true;
            }
        }
        return false;
    }

    /**
     * Method that reduces his health points by points
     * @param points The points to lose
     */
    public void loseHealthPoints(int points) {
        healthPoints -= points;
    }

    // Getters and setters
    /**
     * Method that gets healthPoints
     * @return int This returns healthPoints
     */
    public int getHealthPoints() {
        return healthPoints;
    }

    /**
     * Method that sets healthPoints
     * @param healthPoints The healthPoints to be set
     */
    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    /**
     * Method that gets currentMaxHealthPoints
     * @return int This returns currentMaxHealthPoints
     */
    public int getCurrentMaxHealthPoints() {
        return currentMaxHealthPoints;
    }

    /**
     * Method that sets currentMaxHealthPoints
     * @param currentMaxHealthPoints The currentMaxHealthPoints to be set
     */
    public void setCurrentMaxHealthPoints(int currentMaxHealthPoints) {
        this.currentMaxHealthPoints = currentMaxHealthPoints;
    }

    /**
     * Method that gets currentDamagePoints
     * @return int This returns currentDamagePoints
     */
    public int getCurrentDamagePoints() {
        return currentDamagePoints;
    }

    /**
     * Method that sets currentDamagePoints
     * @param currentDamagePoints The currentDamagePoints to be set
     */
    public void setCurrentDamagePoints(int currentDamagePoints) {
        this.currentDamagePoints = currentDamagePoints;
    }
}
