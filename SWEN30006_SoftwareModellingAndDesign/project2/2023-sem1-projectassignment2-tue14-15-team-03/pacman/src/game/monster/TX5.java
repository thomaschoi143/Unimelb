/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src.game.monster;

import src.game.Game;
import src.game.walkingStrategy.WalkingStrategy;
import src.game.walkingStrategy.WalkingStrategyFactory;

import java.util.Arrays;

public class TX5 extends Monster {
    private final static int initialFreezeSeconds = 5;
    private final static String filename = "m_tx5.gif";

    public TX5(Game game, int seed) {
        super(game, filename, seed, WalkingStrategyFactory.getInstance().getStrategy("Aggressive"));
        freeze(initialFreezeSeconds);
    }

}
