import bagel.*;
import bagel.util.Point;

/**
 * Abstract class that inherits from GameEntity and provides a template for a Projectile
 * Projectiles are Attackable and Movable
 * @author Thomas Choi
 * @version 1.0
 */
public abstract class Projectile extends GameEntity implements Attackable, Movable{
    // The attack range I defined to make the game looks natural
    private final static int ATTACK_RANGE = 30;
    private final double X_DIR, Y_DIR;
    private final double ROTATION;
    private boolean isDead = false;

    /**
     * Method that constructs a Projectile
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     * @param target The Point of the target(sailor)
     * @param name The name(type) of the Projectile
     */
    public Projectile(double x, double y, Point target, String name) {
        super(x, y);
        setCurrentImage(new Image(String.format("res/%1$s/%1$sProjectile.png", name.toLowerCase())));

        ROTATION = Math.atan((target.y - y) / (target.x - x));

        // Set projectile moving direction
        double len = target.distanceTo(new Point(x, y));
        X_DIR = (target.x - x) / len;
        Y_DIR = (target.y - y) / len;
    }

    /**
     * Method that performs state update
     * @param sailor The Sailor of the game level
     * @param topLeftBound The Point of the top left bound
     * @param bottomRightBound The Point of the bottom right bound
     * @return boolean This returns whether the Projectile is still "alive"
     */
    public boolean update(Sailor sailor, Point topLeftBound, Point bottomRightBound) {
        if (isDead || outOfBound(topLeftBound, bottomRightBound)) {
            return false;
        }

        move(X_DIR * getSpeed(), Y_DIR * getSpeed());

        attack(sailor);

        render();
        return true;
    }

    /**
     * Method that renders the projectile with rotation
     */
    @Override
    public void render() {
        getCurrentImage().drawFromTopLeft(getX(), getY(), new DrawOptions().setRotation(ROTATION));
    }

    /**
     * Method that performs the attack to Sailor
     * @param target The target(sailor) to attack
     * @return boolean This returns whether the projectile has attacked the target
     */
    @Override
    public boolean attack(GameEntity target) {
        // Have a check before downcasting
        if (target instanceof Sailor) {
            Sailor sailor = (Sailor) target;
            Point sailorPoint = new Point(sailor.getCenterX(), sailor.getCenterY());
            Point projectilePoint = new Point(getCenterX(), getCenterY());
            double distance = sailorPoint.distanceTo(projectilePoint);

            if (distance < ATTACK_RANGE) {
                sailor.loseHealthPoints(getDamagePoints());
                isDead = true;
                System.out.format("%s inflicts %d damage points on Sailor. Sailor's current health: %d/%d\n",
                        getName(), getDamagePoints(), sailor.getHealthPoints(), sailor.getCurrentMaxHealthPoints());
                return true;
            }
        }
        return false;
    }

    // Abstract methods
    protected abstract String getName();
    protected abstract double getSpeed();
    protected abstract int getDamagePoints();
}
