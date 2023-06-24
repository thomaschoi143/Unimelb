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
import java.util.HashMap;

public class TwoTilesEachPortalRule implements LevelCheckingStrategy{
    public boolean check(GridModel model, File mapFile) {

        HashMap<GridElementType, ArrayList<Location>> portalLocations = new HashMap<>();
        GridElementType currentType;
        GridElementType white = GridElementType.PortalWhiteTile;
        GridElementType yellow = GridElementType.PortalYellowTile;
        GridElementType darkGold = GridElementType.PortalDarkGoldTile;
        GridElementType darkGray = GridElementType.PortalDarkGrayTile;
        boolean noError = true;

        for (int y = 0; y < model.getHeight(); y++) {

            for (int x = 0; x < model.getWidth(); x++) {

                currentType = GridElementType.charToGridElementType(model.getTile(x, y));

                if (currentType == white || currentType == yellow || currentType == darkGold || currentType == darkGray) {

                    if (!portalLocations.containsKey(currentType)){
                        portalLocations.put(currentType, new ArrayList<>());
                    }
                    portalLocations.get(currentType).add(new Location(x+1, y+1));
                }
            }
        }

        for (GridElementType portalColor : portalLocations.keySet()) {

            int portalCounts = portalLocations.get(portalColor).size();

            if (portalCounts != 2){
                String str = "portal ";

                switch (portalColor) {
                    case PortalWhiteTile:
                        str += "White ";
                        break;
                    case PortalYellowTile:
                        str += "Yellow ";
                        break;
                    case PortalDarkGoldTile:
                        str += "DarkGold ";
                        break;
                    case PortalDarkGrayTile:
                        str += "DarkGray ";
                        break;
                }

                str += "count is not 2: ";
                str += loctionsToString(portalLocations.get(portalColor));
                CheckerLogger.getInstance().writeString(mapFile, str);
                noError = false;
            }

        }

        return noError;
    }
}
