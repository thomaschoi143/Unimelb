/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.monster;

import ch.aplu.jgamegrid.*;
import src.game.Game;
import src.game.MovingActor;
import src.game.walkingStrategy.WalkingStrategy;

import java.util.*;

public abstract class Monster extends MovingActor
{
    public final static Location.CompassDirection monsterDefaultDirection = Location.NORTH;
    private final static int horzMirrorOffDegreeMin = 150;
    private final static int horzMirrorOffDegreeMax = 210;
    private MonsterState state = MonsterState.NORMAL;
    protected WalkingStrategy walkingStrategy;

    public Monster(Game game, String filename, int seed, WalkingStrategy walkingStrategy)
    {
      super(game, filename, seed);
      this.walkingStrategy = walkingStrategy;
    }

    // Change the state of the monster to FROZEN for seconds
    public void freeze(int seconds) {
        if (state == MonsterState.FROZEN) {
            return; // Frozen period will not be extended
        }
        state = MonsterState.FROZEN;
        Timer timer = new Timer(); // Instantiate Timer Object
        int SECOND_TO_MILLISECONDS = 1000;
        final Monster monster = this;
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            monster.setState(MonsterState.NORMAL);
          }
        }, seconds * SECOND_TO_MILLISECONDS);
    }

    public void act() {
        if (state == MonsterState.FROZEN) {
            return;
        }
        Double decidedDirection = walkingStrategy.walk(this);
        if (decidedDirection != null) {
            moveWithDirection(getLocation(), decidedDirection);
        }

        getGame().getGameCallback().monsterLocationChanged(this);

        if (getDirection() > horzMirrorOffDegreeMin && getDirection() < horzMirrorOffDegreeMax)
            setHorzMirror(false);
        else
            setHorzMirror(true);
    }

    public void setState(MonsterState state) {
      this.state = state;
    }

    public String getType() {
      return this.getClass().getSimpleName();
    }

}
