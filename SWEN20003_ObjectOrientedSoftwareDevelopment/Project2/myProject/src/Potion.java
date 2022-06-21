/**
 * Class that inherits from Item and provides a template for Potion
 * @author Thomas Choi
 * @version 1.0
 */
public class Potion extends Item{
    private final static int INCREASE_HEALTH_POINTS = 25;
    private final static String NAME = "potion";

    /**
     * Method that constructs a Potion
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public Potion(double x, double y) {
        super(x, y, NAME);
    }

    // Method that let sailor picks up a potion
    @Override
    protected void pickup(SailorLevel1 sailor) {
        int currentHealthPoints = sailor.getHealthPoints();

        currentHealthPoints += INCREASE_HEALTH_POINTS;
        if (currentHealthPoints > sailor.getCurrentMaxHealthPoints()) {
            currentHealthPoints = sailor.getCurrentMaxHealthPoints();
        }
        sailor.setHealthPoints(currentHealthPoints);
        System.out.format("Sailor finds Potion. Sailor's current health: %d/%d\n", sailor.getHealthPoints()
                , sailor.getCurrentMaxHealthPoints());
    }
}
