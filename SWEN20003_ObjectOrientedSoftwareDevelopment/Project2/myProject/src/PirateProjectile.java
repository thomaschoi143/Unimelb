import bagel.util.Point;

/**
 * Class that inherits from Projectile and provides a template for Pirate's projectile
 * @author Thomas Choi
 * @version 1.0
 */
public class PirateProjectile extends Projectile {
    private final static double SPEED = 0.4;

    /**
     * Damage points constant of the PirateProjectile
     */
    public final static int DAMAGE_POINT = 10;

    private final static String NAME = "Pirate";

    /**
     * Method that constructs a PirateProjectile
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     * @param target The Point of the target(sailor)
     */
    public PirateProjectile(double x, double y, Point target) {
        super(x, y, target, NAME);
    }

    // Getters
    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected double getSpeed() {
        return SPEED;
    }

    @Override
    protected int getDamagePoints() {
        return DAMAGE_POINT;
    }
}
