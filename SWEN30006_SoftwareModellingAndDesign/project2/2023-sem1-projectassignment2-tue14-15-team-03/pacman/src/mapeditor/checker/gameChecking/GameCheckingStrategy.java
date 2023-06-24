/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.gameChecking;

import src.utility.CheckerLogger;

import java.io.File;

public interface GameCheckingStrategy {
    boolean check(File folder);
}
