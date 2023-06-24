/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.levelChecking;

import src.utility.CheckerLogger;

import java.util.ArrayList;

public abstract class CompositeLevelChecking implements LevelCheckingStrategy {

    protected ArrayList<LevelCheckingStrategy> rules = new ArrayList<>();

    public void addRule(LevelCheckingStrategy rule) {
        rules.add(rule);
    }
}
