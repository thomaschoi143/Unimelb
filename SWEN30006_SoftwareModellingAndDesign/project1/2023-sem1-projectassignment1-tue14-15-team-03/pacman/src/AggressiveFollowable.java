/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import ch.aplu.jgamegrid.Location;

public interface AggressiveFollowable extends RandomWalkable{
    // Determine direction to target and try to move in that direction. Otherwise, random walk.
    default void aggressiveFollow(Location target) {
        MovingActor actor = (MovingActor)this;
        Location currentLocation = actor.getLocation();
        double oldDirection = actor.getDirection();

        Location.CompassDirection compassDir = currentLocation.get4CompassDirectionTo(target);
        actor.setDirection(compassDir);

        if (!actor.isNextMoveVisited(currentLocation, actor.getDirection()) && actor.canMove(currentLocation, actor.getDirection())) {
            actor.moveWithDirection(currentLocation, actor.getDirection());
        } else {
            actor.setDirection(oldDirection);
            randomWalk();
        }
    }
}
