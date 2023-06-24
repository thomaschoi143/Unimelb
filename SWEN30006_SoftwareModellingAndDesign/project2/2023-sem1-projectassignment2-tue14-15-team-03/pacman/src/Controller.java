/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src;

import src.game.Game;
import src.mapeditor.checker.CheckerFactory;
import src.mapeditor.checker.gameChecking.GameCheckingStrategy;
import src.mapeditor.checker.levelChecking.LevelCheckingStrategy;
import src.mapeditor.editor.EditorController;
import src.game.GameFacade;
import src.mapeditor.editor.EditorState;
import src.mapeditor.grid.GridModel;
import src.utility.XMLParser;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    private static Controller instance;
    private Controller() {}
    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void startApplication(String args[]) {
        if (args.length == 1) {
            File file = new File(args[0]);
            if (file.isDirectory()) {

                startApplicationWithFolder(file);

            } else {
                GridModel model = XMLParser.readFileToModel(file);
                LevelCheckingStrategy levelChecker = CheckerFactory.getInstance().getLevelChecker();
                levelChecker.check(model, file);
                EditorController.getInstance().startEditor(model);
            }
        } else {
            EditorController.getInstance().startEditor();
        }
    }

    private void startApplicationWithFolder(File file) {
        GameCheckingStrategy gameChecker = CheckerFactory.getInstance().getGameChecker();
        if (gameChecker.check(file)) {
            ArrayList<GridModel> models = new ArrayList<>();
            Map<Integer, File> map = new TreeMap<Integer, File>();

            Pattern p = Pattern.compile("^\\d+");
            for (File f : file.listFiles()) {
                Matcher m = p.matcher(f.getName());
                if (m.find() && f.getName().endsWith(".xml")) {
                    map.put(Integer.parseInt(m.group()), f);
                }
            }
            LevelCheckingStrategy levelChecker = CheckerFactory.getInstance().getLevelChecker();
            for (File f : map.values()) {
                GridModel model = XMLParser.readFileToModel(f);
                if (!levelChecker.check(model, f)) {
                    EditorController.getInstance().startEditor(model);
                    return;
                }
                System.out.println("Added to models: "+f.getName());
                models.add(model);
            }

            // Passed level checking, start testing the folder
            EditorController.getInstance().startEditor();
            Runnable testRunnable = new Runnable() {
                public void run() {
                    EditorController.getInstance().setState(EditorState.TEST);
                    GameFacade gameFacade = new GameFacade(models);
                    gameFacade.runGames();
                    EditorController.getInstance().setState(EditorState.EDIT);
                }
            };
            new Thread(testRunnable).start();
        } else {
            EditorController.getInstance().startEditor();
        }

    }
}
