/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Alien extends Monster {
    private final static String filename = "m_alien.gif";

    public Alien(Game game, int seed) {
        super(game, filename, seed);
    }

    // Alien: Shortest Distance Finder
    protected void walkApproach() {
        Location pacLocation = getGame().getPacActorLocation();
        Location currentLocation = getLocation();
        double shortestDistance = Double.MAX_VALUE;

        // Since can have multiple shortest locations, and if so, Alien must randomly choose one location
        HashMap<Location.CompassDirection, Location> shortestLocations = new HashMap<>();

        for (Location.CompassDirection compassDir : Location.CompassDirection.values()) {
            if (canMove(currentLocation, compassDir.getDirection())) {
                Location oneStepFurther = currentLocation.getNeighbourLocation(compassDir);
                double thisDistance = oneStepFurther.getDistanceTo(pacLocation);

                if (thisDistance == shortestDistance) {
                    shortestLocations.put(compassDir, oneStepFurther);

                } else if (thisDistance < shortestDistance) {
                    shortestLocations.clear();
                    shortestLocations.put(compassDir, oneStepFurther);

                    shortestDistance = thisDistance;
                }
            }
        }
        // Randomly select a direction
        ArrayList<Location.CompassDirection> compDirForShortestDirections = new ArrayList<>(shortestLocations.keySet());
        int numOfShortestLocations = compDirForShortestDirections.size();
        Location.CompassDirection randomCompassDirection = compDirForShortestDirections.get(getRandomiser().nextInt(numOfShortestLocations));

        // Set the Alien in that direction
        moveWithDirection(currentLocation, randomCompassDirection.getDirection());
    }
}
