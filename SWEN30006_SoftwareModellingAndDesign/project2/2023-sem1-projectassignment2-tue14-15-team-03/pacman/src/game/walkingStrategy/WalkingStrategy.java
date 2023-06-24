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

public interface WalkingStrategy {
    Double walk(MovingActor actor);
}
