/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import ch.aplu.jgamegrid.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public abstract class MovingActor extends Actor {
    private final static int slowDownFactor = 3;
    private final static int defaultVisitedLocationsLength = 10;
    private final static String spritesFolder = "sprites/";
    private Game game;
    private Random randomiser = new Random();
    private int visitedLocationsLength = defaultVisitedLocationsLength;
    private ArrayList<Location> visitedLocations = new ArrayList<Location>();

    // PacActor
    public MovingActor(Game game, boolean isRotatable, String filename, int nbSprites, int seed) {
        super(isRotatable, spritesFolder + filename, nbSprites);  // Rotatable
        setUpMovingActor(game,seed);
    }

    // Monster
    public MovingActor(Game game, String filename, int seed) {
        super(spritesFolder+ filename);
        setUpMovingActor(game,seed);
    }

    // Orion
    public MovingActor(Game game, String filename, int seed, int visitedLocationsLength) {
        super(spritesFolder + filename);
        this.visitedLocationsLength = visitedLocationsLength;
        setUpMovingActor(game,seed);
    }

    private void setUpMovingActor(Game game, int seed) {
        this.game = game;
        this.randomiser.setSeed(seed);
        setSlowDown(slowDownFactor);
    }

    protected void moveWithDirection(Location currentLocation, double direction) {
        Location oneStepFurther = currentLocation.getNeighbourLocation(direction);
        setLocation(oneStepFurther);
        setDirection(direction);
        addVisitedLocation(getLocation());
    }

    // Check if the next location is not out of bound and not a block
    protected boolean canMove(Location currentLocation, double direction) {
        Location oneStepFurther = currentLocation.getNeighbourLocation(direction);
        BackgroundHandler bgHandler = game.getBackgroundHandler();
        return (!bgHandler.isWall(oneStepFurther) && bgHandler.isInBound(oneStepFurther));
    }

    // Record visited locations
    protected void addVisitedLocation(Location location) {
        visitedLocations.add(location);
        if (visitedLocations.size() == visitedLocationsLength)
            visitedLocations.remove(0); // Only record the last (visitedLocationLength - 1) locations
    }

    // Check if the location is visited before or not
    protected boolean isNextMoveVisited(Location currentLocation, double direction) {
        Location oneStepFurther = currentLocation.getNeighbourLocation(direction);
        for (Location loc : visitedLocations)
            if (loc.equals(oneStepFurther))
                return true;
        return false;
    }

    public Game getGame() {
        return game;
    }

    protected Random getRandomiser() {
        return randomiser;
    }
}
