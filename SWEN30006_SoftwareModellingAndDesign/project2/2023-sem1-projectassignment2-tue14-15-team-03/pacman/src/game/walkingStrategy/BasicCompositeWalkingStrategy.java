/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.walkingStrategy;

import src.game.MovingActor;

public class BasicCompositeWalkingStrategy extends CompositeWalkingStrategy {
    public Double walk(MovingActor actor) {
        Double decidedDirection;
        for (WalkingStrategy strategy : strategies) {
            if ((decidedDirection = strategy.walk(actor)) != null) {
                return decidedDirection;
            }
        }
        return null;
    }
}
