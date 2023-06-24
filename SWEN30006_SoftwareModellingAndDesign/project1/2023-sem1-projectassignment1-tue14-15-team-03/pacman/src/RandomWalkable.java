/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import ch.aplu.jgamegrid.*;

public interface RandomWalkable {
    int turnRightAngle = 90;
    int turnBackward = 180;
    int positive = 1;
    int negative = -1;
    // Randomly turn left or right and move. If can't, go forward or turn the other side. Otherwise, go backward
    default void randomWalk() {
        MovingActor actor = (MovingActor)this;
        double oldDirection = actor.getDirection();
        Location currentLocation = actor.getLocation();

        int sign = actor.getRandomiser().nextDouble() < 0.5 ? positive : negative;
        actor.turn(sign * turnRightAngle);  // Try to turn left/right

        if (actor.canMove(currentLocation, actor.getDirection())) {
            actor.moveWithDirection(currentLocation, actor.getDirection());
        } else {
            actor.setDirection(oldDirection);
            if (actor.canMove(currentLocation, actor.getDirection())) {
                // Try to move forward
                actor.moveWithDirection(currentLocation, actor.getDirection());
            } else {
                actor.setDirection(oldDirection);
                actor.turn(-sign * turnRightAngle);  // Try to turn right/left
                if (actor.canMove(currentLocation, actor.getDirection())) {
                    actor.moveWithDirection(currentLocation, actor.getDirection());
                } else {
                    actor.setDirection(oldDirection);
                    actor.turn(turnBackward);  // Turn backward
                    if (actor.canMove(currentLocation, actor.getDirection())) {
                        actor.moveWithDirection(currentLocation, actor.getDirection());
                    }
                }
            }
        }
    }
}
