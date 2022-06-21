import bagel.util.Point;

/**
 * Class that inherits from Projectile to provide a template for Blackbeard's projectile
 * @author Thomas Choi
 * @version 1.0
 */
public class BlackbeardProjectile extends Projectile{
    private final static double SPEED = 0.8;
    private final static int DAMAGE_POINT = PirateProjectile.DAMAGE_POINT * 2;
    private final static String NAME = "Blackbeard";

    /**
     * Method that constructs a BlackbeardProjectile
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     * @param target The Point of the target(sailor)
     */
    public BlackbeardProjectile(double x, double y, Point target) {
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
