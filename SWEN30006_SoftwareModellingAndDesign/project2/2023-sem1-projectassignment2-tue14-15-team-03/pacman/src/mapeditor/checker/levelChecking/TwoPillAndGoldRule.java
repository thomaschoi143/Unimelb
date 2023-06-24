/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.levelChecking;

import src.gameGrid.GridElementType;
import src.mapeditor.grid.GridModel;
import src.utility.CheckerLogger;

import java.io.File;

public class TwoPillAndGoldRule implements LevelCheckingStrategy{
    public boolean check(GridModel model, File mapFile) {

        GridElementType currentType;
        int pillAndGoldCounter = 0;

        for (int y = 0; y < model.getHeight(); y++) {

            for (int x = 0; x < model.getWidth(); x++) {

                currentType = GridElementType.charToGridElementType(model.getTile(x, y));

                if (currentType == GridElementType.PillTile || currentType == GridElementType.GoldTile) {
                    pillAndGoldCounter++;
                }
            }
        }

        if (pillAndGoldCounter < 2){

            String str = "less than 2 Gold and Pill";

            CheckerLogger.getInstance().writeString(mapFile, str);

            return false;
        }

        return true;
    }


}
