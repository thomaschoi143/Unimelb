import bagel.util.Point;

/**
 * Class that inherits from Enemy and provides a template for Pirate
 * @author Thomas Choi
 * @version 1.0
 */
public class Pirate extends Enemy {
    /**
     * Attack range constant of the Pirate
     */
    public final static int ATTACK_RANGE = 100;
    /**
     * Cooldown time constant in ms of the Pirate
     */
    public final static int COOLDOWN_MS = 3000;
    /**
     * Max health points constant of the Pirate
     */
    public final static int MAX_HEALTH_POINTS = 45;

    private final static String NAME = "Pirate";

    /**
     * Method that constructs a Pirate
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public Pirate(double x, double y) {
        super(x, y, MAX_HEALTH_POINTS, NAME);
    }

    // Method that performs firing a pirate projectile
    @Override
    protected Projectile fireProjectile(Point sailor) {
        return new PirateProjectile(getX(), getY(), sailor);
    }

    // Getters
    @Override
    protected int getAttackRange() {
        return ATTACK_RANGE;
    }

    @Override
    protected int getMaxHealthPoints() {
        return MAX_HEALTH_POINTS;
    }

    @Override
    protected int getCooldownMs() {
        return COOLDOWN_MS;
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
