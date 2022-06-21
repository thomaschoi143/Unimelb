import bagel.util.Point;

/**
 * Interface that is implemented to the movable game entities(Enemy, Sailor and Projectile)
 */
public interface Movable {
    /**
     * Default method to perform the movement
     * @param xMove The x coordinate that it moves to
     * @param yMove The y coordinate that it moves to
     */
    default void move(double xMove, double yMove) {
        GameEntity gameEntity = (GameEntity)this;

        gameEntity.setX(gameEntity.getX() + xMove);
        gameEntity.setY(gameEntity.getY() + yMove);
    }

    /**
     * Default method that checks if it is out of bound
     * @param topLeftBound The Point of the top left bound
     * @param bottomRightBound The Point of the bottom right bound
     * @return boolean This returns whether it is out of bound
     */
    default boolean outOfBound(Point topLeftBound, Point bottomRightBound) {
        double x = ((GameEntity)this).getX();
        double y = ((GameEntity)this).getY();

        return x < topLeftBound.x || x > bottomRightBound.x || y < topLeftBound.y || y > bottomRightBound.y;
    }
}
