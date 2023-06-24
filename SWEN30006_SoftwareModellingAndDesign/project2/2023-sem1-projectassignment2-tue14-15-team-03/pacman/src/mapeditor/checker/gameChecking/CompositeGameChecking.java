/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.gameChecking;

import src.utility.CheckerLogger;

import java.io.File;
import java.util.ArrayList;

public abstract class CompositeGameChecking implements GameCheckingStrategy{
    protected ArrayList<GameCheckingStrategy> rules = new ArrayList<>();

    public void addRule(GameCheckingStrategy rule) {
        rules.add(rule);
    }
}
