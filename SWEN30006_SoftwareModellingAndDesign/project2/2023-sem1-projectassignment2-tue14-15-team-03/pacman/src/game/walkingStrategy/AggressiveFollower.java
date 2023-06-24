/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.walkingStrategy;

import ch.aplu.jgamegrid.Location;
import src.game.MovingActor;

public class AggressiveFollower implements WalkingStrategy {
    public Double walk(MovingActor actor) {
        Location currentLocation = actor.getLocation();
        
        Location target = actor.getGame().getPacActor().getLocation();
        double oldDirection = actor.getDirection();

        Location.CompassDirection compassDir = currentLocation.get4CompassDirectionTo(target);
        actor.setDirection(compassDir);

        if (!actor.isNextMoveVisited(currentLocation, actor.getDirection()) && actor.canMove(currentLocation, actor.getDirection())) {
            return actor.getDirection();
        } else {
            actor.setDirection(oldDirection);
            return null;
        }
    }
}
