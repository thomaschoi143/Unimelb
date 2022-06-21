import bagel.util.Point;

/**
 * Class that inherits from Enemy to provide a template for Blackbeard
 * @author Thomas Choi
 * @version 1.0
 */
public class Blackbeard extends Enemy{
    private final static int ATTACK_RANGE = Pirate.ATTACK_RANGE * 2;
    private final static int COOLDOWN_MS = Pirate.COOLDOWN_MS / 2;
    private final static int MAX_HEALTH_POINTS = Pirate.MAX_HEALTH_POINTS * 2;
    private final static String NAME = "Blackbeard";

    /**
     * Method that constructs a Blackbeard
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public Blackbeard (double x, double y) {
        super(x, y, MAX_HEALTH_POINTS, NAME);
    }

    // Method that performs firing a blackbeard projectile
    @Override
    protected Projectile fireProjectile(Point sailor) {
        return new BlackbeardProjectile(getX(), getY(), sailor);
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
