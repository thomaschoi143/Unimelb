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

public class Troll extends Monster {
    private final static String filename = "m_troll.gif";
    public Troll(Game game, int seed) {
        super(game, filename, seed, WalkingStrategyFactory.getInstance().getStrategy("Random"));
    }
}
