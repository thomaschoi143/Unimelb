/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import ch.aplu.jgamegrid.*;

import java.awt.event.KeyEvent;

public class PacActorManual extends PacActor implements GGKeyRepeatListener {
    public PacActorManual(Game game, int seed) {
        super(game, seed);
    }

    public void keyRepeated(int keyCode) {
        if (isRemoved())  // Already removed
            return;
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                setDirection(Location.WEST);
                break;
            case KeyEvent.VK_UP:
                setDirection(Location.NORTH);
                break;
            case KeyEvent.VK_RIGHT:
                setDirection(Location.EAST);
                break;
            case KeyEvent.VK_DOWN:
                setDirection(Location.SOUTH);
                break;
        }
        if (canMove(getLocation(), getDirection())) {
            moveWithDirection(getLocation(), getDirection());
        }
    }
}
