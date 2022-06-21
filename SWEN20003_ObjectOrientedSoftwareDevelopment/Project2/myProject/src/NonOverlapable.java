import bagel.util.Rectangle;

/**
 * Interface that is implemented to the non-overlapable game entities(Block and Bomb)
 */
public interface NonOverlapable {

    /**
     * Default method that checks if it collides with a rebounceableObj
     * @param rebounceableObj Game entity that can rebounce
     * @return This returns whether the object has overlapped the rebounceableObj
     */
    default boolean nonOverlap(Rebounceable rebounceableObj) {
        GameEntity gameEntity = (GameEntity)rebounceableObj;
        GameEntity blockGameEntity = (GameEntity)this;

        Rectangle blockRec = new Rectangle(blockGameEntity.getX(), blockGameEntity.getY(),
                blockGameEntity.getCurrentImage().getWidth(), blockGameEntity.getCurrentImage().getHeight());
        Rectangle gameEntityRec = new Rectangle(gameEntity.getX(), gameEntity.getY(), gameEntity.getCurrentImage().getWidth(),
                gameEntity.getCurrentImage().getHeight());

        if (blockRec.intersects(gameEntityRec)) {
            rebounceableObj.rebounce();
            return true;
        }
        return false;
    }
}
