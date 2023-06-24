/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src;

import ch.aplu.jgamegrid.*;
import java.util.*;

public abstract class Monster extends MovingActor
{
    public final static Location.CompassDirection monsterDefaultDirection = Location.NORTH;
    private final static int furiousMSeconds = 3000;
    private final static int frozenSeconds = 3;
    private final static int horzMirrorOffDegreeMin = 150;
    private final static int horzMirrorOffDegreeMax = 210;

    private MonsterState state = MonsterState.NORMAL;

    public Monster(Game game, String filename, int seed)
    {
      super(game, filename, seed);
    }
    public Monster(Game game, String filename, int seed, int visitedLocationLength) {
        super(game, filename, seed, visitedLocationLength);
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

    // Change the state of the monster to FURIOUS for furiousMSeconds
    public void walkFuriously() {
        if (state != MonsterState.NORMAL) {
            // state is FURIOUS (furious period will not be extended) or FROZEN
            return;
        }
        state = MonsterState.FURIOUS;
        Timer timer = new Timer(); // Instantiate Timer Object
        final Monster monster = this;
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            if (monster.getState() == MonsterState.FURIOUS) {
                monster.setState(MonsterState.NORMAL);
            }
          }
        }, furiousMSeconds);
    }

    public void act() {
        if (state == MonsterState.FROZEN) {
            return;
        }

        walkApproach(); // New location and direction are set

        getGame().getGameCallback().monsterLocationChanged(this);

        if (getDirection() > horzMirrorOffDegreeMin && getDirection() < horzMirrorOffDegreeMax)
            setHorzMirror(false);
        else
            setHorzMirror(true);
    }

    protected void moveWithDirection(Location currentLocation, double direction) {
        if (state == MonsterState.FURIOUS) {
            Location oneStepFurther = currentLocation.getNeighbourLocation(direction);
            addVisitedLocation(oneStepFurther);
            super.moveWithDirection(oneStepFurther, direction);
        } else {
            super.moveWithDirection(currentLocation, direction);
        }
    }

    // Check if the location is movable. If monster is furious, check 1 step further
    protected boolean canMove(Location currentDirection, double direction) {
        if (state == MonsterState.FURIOUS) {
            Location oneStepFurther = currentDirection.getNeighbourLocation(direction);
            return (super.canMove(currentDirection, direction) && super.canMove(oneStepFurther, direction));
        }
        return super.canMove(currentDirection, direction);
    }

    protected boolean isNextMoveVisited(Location currentLocation, double direction) {
        if (state == MonsterState.FURIOUS) {
            Location oneStepFurther = currentLocation.getNeighbourLocation(direction);
            return (super.isNextMoveVisited(currentLocation, direction) && super.isNextMoveVisited(oneStepFurther, direction));
        }
        return super.isNextMoveVisited(currentLocation, direction);
    }

    protected abstract void walkApproach();

    public void setState(MonsterState state) {
      this.state = state;
    }

    public int getFrozenSeconds() {
      return frozenSeconds;
    }

    public String getType() {
      return this.getClass().getSimpleName();
    }

    public MonsterState getState() {
      return state;
    }

}
