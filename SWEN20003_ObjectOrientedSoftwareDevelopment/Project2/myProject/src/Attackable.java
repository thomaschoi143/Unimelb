/**
 * Interface that is implemented to the attackable game entites (Enemy, Bomb, Sailor, Projectile)
 */
public interface Attackable {
    /**
     * Method that determines if the object can attack the target
     * @param target The target to attack
     * @return boolean This returns whether the object can attack the target
     */
    boolean attack(GameEntity target);
}
