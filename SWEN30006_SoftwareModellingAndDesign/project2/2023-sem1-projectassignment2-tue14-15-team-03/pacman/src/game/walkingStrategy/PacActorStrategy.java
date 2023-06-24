/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.walkingStrategy;

import ch.aplu.jgamegrid.Location;
import src.game.BackgroundHandler;
import src.game.Game;
import src.game.MovingActor;
import src.game.item.Item;
import src.game.pacActor.PacActorAuto;
import src.utility.PathFindingFacade;
import src.gameGrid.GridElementType;
import src.gameGrid.PacGameGrid;

import java.util.List;

public class PacActorStrategy implements WalkingStrategy {
    private Location target;
    private PathFindingFacade pathFindingFacade;
    public Double walk(MovingActor actor) {
        if (!(actor instanceof PacActorAuto)) {
            return null;
        }
        PacActorAuto pacActorAuto = (PacActorAuto) actor;

        Game game = pacActorAuto.getGame();
        BackgroundHandler backgroundHandler = game.getBackgroundHandler();
        if (pathFindingFacade == null) {
            pathFindingFacade = new PathFindingFacade(game.getModel());
        }
        if (target == null || target.equals(pacActorAuto.getLocation())) {
            // Get a new target
            target = closestItemLocation(pacActorAuto.getLocation(), game.getPillsAndGolds(), backgroundHandler);
        }
        Location nextLocation = pathFindingFacade.findPath(pacActorAuto.getLocation(), target);
        if (nextLocation == null) {
            return null;
        }

        if (backgroundHandler.isPortal(pacActorAuto.getLocation()) && backgroundHandler.isPortal(nextLocation)) {
            // Have to move off the portal to go back
            nextLocation = getWalkableNeighbourLocation(pacActorAuto.getLocation(), game.getModel());
        }
        if (nextLocation == null) {
            return null;
        }
        return pacActorAuto.getLocation().getDirectionTo(nextLocation);
    }

    private Location closestItemLocation(Location actorLocation, List<Item> targets, BackgroundHandler backgroundHandler) {
        int currentDistance = Integer.MAX_VALUE;
        Location currentLocation = null;
        for (Item target: targets) {
            if (backgroundHandler.isItemLocation(target.getLocation())) {
                // The item has not been eaten yet
                int distance = target.getLocation().getDistanceTo(actorLocation);
                if (distance < currentDistance) {
                    currentLocation = target.getLocation();
                    currentDistance = distance;
                }
            }
        }
        return currentLocation;
    }

    private Location getWalkableNeighbourLocation(Location location, PacGameGrid model) {
        for (Location.CompassDirection dir : Location.CompassDirection.values()) {
            Location neighbourLoc = location.getNeighbourLocation(dir);
            int x = neighbourLoc.getX();
            int y = neighbourLoc.getY();
            if (model.isInBound(x, y) && GridElementType.charToGridElementType(model.getTile(x, y)) != GridElementType.WallTile) {
                return neighbourLoc;
            }
        }
        return null;
    }


}
