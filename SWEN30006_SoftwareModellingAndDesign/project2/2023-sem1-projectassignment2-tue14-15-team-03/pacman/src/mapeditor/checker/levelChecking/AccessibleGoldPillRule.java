/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.levelChecking;

import ch.aplu.jgamegrid.Location;
import src.gameGrid.GridElementType;
import src.utility.CheckerLogger;
import src.mapeditor.grid.GridModel;
import src.utility.PathFindingFacade;

import java.io.File;
import java.util.ArrayList;

public class AccessibleGoldPillRule implements LevelCheckingStrategy {


    public boolean check(GridModel model, File mapFile) {
        ArrayList<Location> inaccessibleGolds = new ArrayList<>();
        ArrayList<Location> inaccessiblePills = new ArrayList<>();
        ArrayList<Location> pacMans = getPacManLoc(model);

        if (pacMans.size() != 1) {
            // No need to check if golds and pills are accessible
            return true;
        }

        PathFindingFacade pathFindingFacade = new PathFindingFacade(model);
        boolean isPass = true;
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                Location loc = new Location(x, y);
                GridElementType type = GridElementType.charToGridElementType(model.getTile(x, y));
                if (type == GridElementType.GoldTile && (pathFindingFacade.findPath(pacMans.get(0), loc) == null)) {
                    inaccessibleGolds.add(new Location(x+1, y+1));
                } else if (type == GridElementType.PillTile && (pathFindingFacade.findPath(pacMans.get(0), loc) == null)) {
                    inaccessiblePills.add(new Location(x+1, y+1));
                }
            }
        }
        if (!inaccessibleGolds.isEmpty()) {
            String str = "Gold not accessible: ";
            str += loctionsToString(inaccessibleGolds);
            CheckerLogger.getInstance().writeString(mapFile, str);
            isPass = false;
        }
        if (!inaccessiblePills.isEmpty()) {
            String str = "Pill not accessible: ";
            str += loctionsToString(inaccessiblePills);
            CheckerLogger.getInstance().writeString(mapFile, str);
            isPass = false;
        }
        return isPass;
    }

    private ArrayList<Location> getPacManLoc(GridModel model) {
        ArrayList<Location> locations = new ArrayList<>();
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                if (GridElementType.charToGridElementType(model.getTile(x, y)) == GridElementType.PacTile) {
                    locations.add(new Location(x, y));
                }
            }
        }
        return locations;
    }
}
