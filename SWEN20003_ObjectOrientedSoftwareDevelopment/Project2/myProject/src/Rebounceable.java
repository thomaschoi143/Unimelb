/**
 * Interface that is implemented to rebounceable objects (Enemy, Sailor)
 * Rebounceable means it is also Movable
 */
public interface Rebounceable extends Movable{
    /**
     * Method that performs the rebounce movement
     */
    void rebounce();
}
