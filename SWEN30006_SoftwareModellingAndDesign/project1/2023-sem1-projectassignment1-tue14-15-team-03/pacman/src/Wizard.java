/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import ch.aplu.jgamegrid.Location;

import java.awt.*;

public class Wizard extends Monster{

    private final static String filename = "m_wizard.gif";

    public Wizard(Game game, int seed) {
        super(game, filename, seed);
    }

    // Wall-Through Walker: Wizard will only jump a wall when the destination(n+1 for normal / n+2 for furious) is a wall
    protected void walkApproach() {
        Location currentLocation = getLocation();
        BackgroundHandler backgroundHandler = getGame().getBackgroundHandler();
        Location.CompassDirection chosenDirection;
        Location firstStep, destination, finalDestination;

        while (true) {
            chosenDirection = randomlyChooseDirection();
            firstStep =  currentLocation.getNeighbourLocation(chosenDirection);

            if (getState() == MonsterState.FURIOUS) {
                if (!backgroundHandler.isWall(firstStep) && backgroundHandler.isInBound(firstStep)) {
                    destination = firstStep.getNeighbourLocation(chosenDirection);  // destination = n + 2
                } else {
                    continue;
                }
            } else {
                destination = firstStep; // destination = n + 1
            }

            if (backgroundHandler.isWall(destination)) {
                // Check if it can jump the wall
                Location afterWall = destination.getNeighbourLocation(chosenDirection);
                if (!backgroundHandler.isWall(afterWall) && backgroundHandler.isInBound(afterWall)) {
                    finalDestination = afterWall;
                    break;
                }
            } else if (backgroundHandler.isInBound(destination)) {
                // No need to jump wall
                finalDestination = destination;
                break;
            }

        }

        if (getState() == MonsterState.FURIOUS) {
            addVisitedLocation(firstStep);
        }
        addVisitedLocation(finalDestination);

        setDirection(chosenDirection);
        setLocation(finalDestination);
    }

    // Randomly select a direction from the neighbour directions (the 8 directions)
    private Location.CompassDirection randomlyChooseDirection() {
        Location.CompassDirection[] directions = Location.CompassDirection.values();
        int chosenDirectionIndex = getRandomiser().nextInt(directions.length);

        return directions[chosenDirectionIndex];
    }

}
