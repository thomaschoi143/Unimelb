/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

public class Troll extends Monster implements RandomWalkable {
    private final static String filename = "m_troll.gif";
    public Troll(Game game, int seed) {
        super(game, filename, seed);
    }

    protected void walkApproach() {
        randomWalk();
    }
}
