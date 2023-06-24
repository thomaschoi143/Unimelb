/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src.game.pacActor;

import src.game.Game;
import src.game.walkingStrategy.WalkingStrategy;
import src.game.walkingStrategy.WalkingStrategyFactory;

public class PacActorAuto extends PacActor {
    private WalkingStrategy walkingStrategy;

    public PacActorAuto(Game game, int seed) {
        super(game, seed);
        this.walkingStrategy = WalkingStrategyFactory.getInstance().getStrategy("PacActor");
    }

    public void act() {
        moveWithDirection(getLocation(), walkingStrategy.walk(this));
        super.act();
    }
}
