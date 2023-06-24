/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import ch.aplu.jgamegrid.*;

import java.util.ArrayList;

public class TX5 extends Monster implements AggressiveFollowable {
    private final static int initialFreezeSeconds = 5;
    private final static String filename = "m_tx5.gif";

    public TX5(Game game, int seed) {
        super(game, filename, seed);
        freeze(initialFreezeSeconds);
    }

    // TX5: Aggressive Follower with pacActor as the target
    protected void walkApproach() {
        aggressiveFollow(getGame().getPacActorLocation());
    }

}
