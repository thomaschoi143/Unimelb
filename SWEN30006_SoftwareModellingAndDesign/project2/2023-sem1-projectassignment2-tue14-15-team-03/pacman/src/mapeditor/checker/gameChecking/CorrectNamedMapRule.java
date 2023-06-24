/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.gameChecking;

import src.utility.CheckerLogger;

import java.io.File;

public class CorrectNamedMapRule implements GameCheckingStrategy {
    public boolean check(File folder) {
        File[] files = folder.listFiles();
        for (File file : files) {
            if (Character.isDigit(file.getName().charAt(0)) && file.getName().endsWith(".xml")) {
                return true;
            }
        }
        CheckerLogger.getInstance().writeString(folder, "no maps found");
        return false;
    }
}
