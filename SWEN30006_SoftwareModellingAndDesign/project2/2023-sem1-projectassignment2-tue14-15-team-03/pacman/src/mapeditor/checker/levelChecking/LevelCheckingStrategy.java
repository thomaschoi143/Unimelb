/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.levelChecking;

import ch.aplu.jgamegrid.Location;
import src.utility.CheckerLogger;
import src.mapeditor.grid.GridModel;

import java.io.File;
import java.util.ArrayList;

public interface LevelCheckingStrategy {
    boolean check(GridModel model, File mapFile);
    default String loctionsToString(ArrayList<Location> locations) {
        String str = "";
        for(int i = 0; i < locations.size(); i++) {
            str += locations.get(i).toString().replace(" ", "");
            if (i != locations.size() - 1) {
                str += "; ";
            }
        }
        return str;
    }
}
