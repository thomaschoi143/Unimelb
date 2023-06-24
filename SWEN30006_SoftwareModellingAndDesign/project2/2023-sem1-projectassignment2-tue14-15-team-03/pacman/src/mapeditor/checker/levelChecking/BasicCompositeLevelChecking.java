/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.levelChecking;

import src.utility.CheckerLogger;
import src.mapeditor.grid.GridModel;

import java.io.File;

public class BasicCompositeLevelChecking extends CompositeLevelChecking {
    public boolean check(GridModel model, File mapFile) {
        boolean isPass = true;
        for (LevelCheckingStrategy rule : rules) {
            if (!rule.check(model, mapFile)) {
                isPass = false;
            }
        }
        return isPass;
    }
}
