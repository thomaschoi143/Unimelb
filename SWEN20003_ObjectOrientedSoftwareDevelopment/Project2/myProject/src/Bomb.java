import bagel.*;

/**
 * Class that inherits from GameEntity to provide a template for Bomb
 * Bombs in level1 are NonOverlapable and Attackable
 * @author Thomas Choi
 * @version 1.0
 */
public class Bomb extends GameEntity implements NonOverlapable, Attackable{
    private final static int DAMAGE_POINT = 10;
    private final static int EXPLOSION_TIME_MS = 500;
    private final static String FILENAME = "res/bomb.png";

    private final Image EXPLOSION_IMAGE = new Image("res/explosion.png");
    private boolean inExplosion = false;
    private int explosionTimeCounter = 0;

    /**
     * Method that constructs a Bomb
     * @param x The initial x coordinate of the top left
     * @param y The initial y coordinate of the top left
     */
    public Bomb(double x, double y) {
        super(x, y);
        setCurrentImage(new Image(FILENAME));
    }

    /**
     * Method that performs state update
     * @return boolean This returns whether the bomb has not exploded
     */
    public boolean update() {
        if (inExplosion) {
            explosionTimeCounter++;
            if (explosionTimeCounter / (REFRESH_RATE / 1000.0) == EXPLOSION_TIME_MS) {
                inExplosion = false;
                explosionTimeCounter = 0;
                return false;
            }
        }
        render();
        return true;
    }

    /**
     * Method that overrides the nonOverlap method in NonOverlapable
     * and performs attack behaviour at the same time
     * @param rebounceableObj Game entity that can rebounce
     * @return boolean This returns whether the bomb has overlapped the rebounceableObj
     */
    @Override
    public boolean nonOverlap(Rebounceable rebounceableObj) {
        if (NonOverlapable.super.nonOverlap(rebounceableObj)) {
            attack((GameEntity) rebounceableObj);

            return true;
        }
        return false;
    }

    /**
     * Method that determines if the bomb can attack the sailor
     * @param target The target(sailor) to attack
     * @return boolean This returns whether the bomb has attacked the target
     */
    @Override
    public boolean attack(GameEntity target) {
        // Check the bomb is still attackable and make sure the target is the sailor before downcasting
        if(!inExplosion && target instanceof Sailor) {
            Sailor sailor = (Sailor)target;
            sailor.loseHealthPoints(DAMAGE_POINT);
            inExplosion = true;
            setCurrentImage(EXPLOSION_IMAGE);

            System.out.format("Boom inflicts %d damage points on Sailor. Sailor's current health: %d/%d\n", DAMAGE_POINT,
                    sailor.getHealthPoints(), sailor.getCurrentMaxHealthPoints());
            return true;
        }
        return false;
    }
}
