/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacActorAuto extends PacActor implements AggressiveFollowable {
    private List<String> propertyMoves;
    private int propertyMoveIndex = 0;
    private ArrayList<Location> itemsLocations;

    public PacActorAuto(Game game, int seed, List<String> propertyMoves, ArrayList<Location> pillsAndItemLocations) {
        super(game, seed);
        this.propertyMoves = propertyMoves;
        this.itemsLocations = pillsAndItemLocations;
    }

    public void act() {
        moveInAutoMode();
        super.act();
    }

    private Location closestItemLocation() {
        int currentDistance = Integer.MAX_VALUE;
        Location currentLocation = null;
        for (Location location: itemsLocations) {
            int distanceToPill = location.getDistanceTo(getLocation());
            if (distanceToPill < currentDistance) {
                currentLocation = location;
                currentDistance = distanceToPill;
            }
        }
        return currentLocation;
    }


    public void followPropertyMoves() {
        String currentMove = propertyMoves.get(propertyMoveIndex);
        switch (currentMove) {
            case "R":
                turn(90);
                break;
            case "L":
                turn(-90);
                break;
            case "M":
                Location currentLocation = getLocation();
                double currentDirection = getDirection();
                if (canMove(currentLocation, currentDirection)) {
                    moveWithDirection(currentLocation, currentDirection);
                }
                break;
        }
        propertyMoveIndex++;
    }


    public void moveInAutoMode() {
        if (propertyMoves.size() > propertyMoveIndex) {
            followPropertyMoves();
            return;
        }
        Location closestPill = closestItemLocation();

        aggressiveFollow(closestPill);
    }
}
