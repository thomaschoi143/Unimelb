/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.walkingStrategy;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import src.game.MovingActor;

import java.util.List;

public class RandomWalk implements WalkingStrategy {
    private final static int turnRightAngle = 90;
    private final static int turnBackward = 180;
    private final static int positive = 1;
    private final static int negative = -1;

    public Double walk(MovingActor actor) {
        double oldDirection = actor.getDirection();
        Location currentLocation = actor.getLocation();

        int sign = actor.getRandomiser().nextDouble() < 0.5 ? positive : negative;
        actor.turn(sign * turnRightAngle);  // Try to turn left/right

        if (actor.canMove(currentLocation, actor.getDirection())) {
            return actor.getDirection();
        } else {
            actor.setDirection(oldDirection);
            if (actor.canMove(currentLocation, actor.getDirection())) {
                // Try to move forward
                return actor.getDirection();
            } else {
                actor.setDirection(oldDirection);
                actor.turn(-sign * turnRightAngle);  // Try to turn right/left
                if (actor.canMove(currentLocation, actor.getDirection())) {
                    return actor.getDirection();
                } else {
                    actor.setDirection(oldDirection);
                    actor.turn(turnBackward);  // Turn backward
                    if (actor.canMove(currentLocation, actor.getDirection())) {
                        return actor.getDirection();
                    }
                }
            }
        }
        return null;
    }
}
