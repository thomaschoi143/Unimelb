
/**
 * Class that inherits from Item and provides a template for Elixir
 * @author Thomas Choi
 * @version 1.0
 */
public class Elixir extends Item {
    private final static int INCREASE_MAX_HEALTH_POINTS = 35;
    private final static String NAME = "elixir";

    /**
     * Method that constructs a Elixir
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public Elixir(double x, double y) {
        super(x, y, NAME);
    }

    // Method that let sailor pick up an elixir
    @Override
    protected void pickup(SailorLevel1 sailor) {
        int newMaxHealthPoints = sailor.getCurrentMaxHealthPoints() + INCREASE_MAX_HEALTH_POINTS;
        sailor.setHealthPoints(newMaxHealthPoints);
        sailor.setCurrentMaxHealthPoints(newMaxHealthPoints);

        System.out.format("Sailor finds Elixir. Sailor's current health: %d/%d\n", sailor.getHealthPoints()
                , sailor.getCurrentMaxHealthPoints());
    }
}
