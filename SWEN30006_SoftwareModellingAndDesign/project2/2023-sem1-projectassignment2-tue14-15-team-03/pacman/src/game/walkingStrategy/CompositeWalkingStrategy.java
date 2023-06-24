/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.walkingStrategy;

import java.util.ArrayList;

public abstract class CompositeWalkingStrategy implements WalkingStrategy{
    protected ArrayList<WalkingStrategy> strategies = new ArrayList<>();

    public void addStrategy(WalkingStrategy strategy) {
        strategies.add(strategy);
    }
}
