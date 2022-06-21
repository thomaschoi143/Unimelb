/**
 * Class that inherits from Item and provides a template for Sword
 * @author Thomas Choi
 * @version 1.0
 */
public class Sword extends Item{
    private final static int INCREASE_DAMAGE_POINTS = 15;
    private final static String NAME = "sword";

    /**
     * Method that constructs a Sword
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public Sword(double x, double y) {
        super(x, y, NAME);
    }

    // Method that let sailor picks up a sword
    @Override
    protected void pickup(SailorLevel1 sailor) {
        sailor.setCurrentDamagePoints(sailor.getCurrentDamagePoints() + INCREASE_DAMAGE_POINTS);
        System.out.format("Sailor finds Sword. Sailor's damage points increased to : %d\n", sailor.getCurrentDamagePoints());
    }
}
