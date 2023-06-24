/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker;

import src.mapeditor.checker.gameChecking.BasicCompositeGameChecking;
import src.mapeditor.checker.gameChecking.CorrectNamedMapRule;
import src.mapeditor.checker.gameChecking.OneMapPerNumberRule;
import src.mapeditor.checker.gameChecking.GameCheckingStrategy;
import src.mapeditor.checker.levelChecking.*;
import src.utility.CheckerLogger;

public class CheckerFactory {
    private static CheckerFactory instance;

    private CheckerFactory() {
    }

    public static CheckerFactory getInstance() {
        if (instance == null) {
            instance = new CheckerFactory();
        }
        return instance;
    }

    public LevelCheckingStrategy getLevelChecker() {
        BasicCompositeLevelChecking basicComposite =  new BasicCompositeLevelChecking();
        basicComposite.addRule(new PacManStartPointRule());
        basicComposite.addRule(new TwoTilesEachPortalRule());
        basicComposite.addRule(new TwoPillAndGoldRule());
        basicComposite.addRule(new AccessibleGoldPillRule());
        return basicComposite;
    }
    public GameCheckingStrategy getGameChecker() {
        BasicCompositeGameChecking basicComposite = new BasicCompositeGameChecking();
        basicComposite.addRule(new CorrectNamedMapRule());
        basicComposite.addRule(new OneMapPerNumberRule());
        return basicComposite;
    }
}
