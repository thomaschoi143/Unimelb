/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.gameChecking;

import src.utility.CheckerLogger;

import java.io.File;

public class BasicCompositeGameChecking extends CompositeGameChecking {

    public boolean check(File folder) {
        boolean isPass = true;
        for (GameCheckingStrategy rule : rules) {
            if (!rule.check(folder)) {
                isPass = false;
            }
        }
        return isPass;
    }
}
