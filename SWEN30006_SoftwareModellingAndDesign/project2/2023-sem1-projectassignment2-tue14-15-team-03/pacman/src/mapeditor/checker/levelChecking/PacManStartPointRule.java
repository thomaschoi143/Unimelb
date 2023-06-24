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

import java.io.File;
import java.util.ArrayList;

public class PacManStartPointRule implements LevelCheckingStrategy {
    public boolean check(GridModel model, File mapFile) {
        ArrayList<Location> pacManLocations = new ArrayList<>();
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                if (GridElementType.charToGridElementType(model.getTile(x, y)) == GridElementType.PacTile) {
                    pacManLocations.add(new Location(x+1, y+1));
                }
            }
        }
        if (pacManLocations.size() != 1) {
            String str;
            if (pacManLocations.size() == 0) {
                str = "no start for PacMan";
            } else {
                str = "more than one start for Pacman: ";
                str += loctionsToString(pacManLocations);
            }
            CheckerLogger.getInstance().writeString(mapFile, str);
            return false;
        }
        return true;
    }
}
