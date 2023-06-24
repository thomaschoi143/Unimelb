/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src.game;

import src.gameGrid.PacGameGrid;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import java.util.*;

public class GameFacade {
    private static final String defaultPropertiesPath = "properties/test.properties";
    private int seed;
    private boolean isPacActorAuto;
    private ArrayList<Game> games = new ArrayList<>();
    private GameCallback gameCallback = new GameCallback();

    public GameFacade(PacGameGrid model) {
        setUpProperties();
        Game game = new Game(this, model, true);
        games.add(game);
    }

    public GameFacade(ArrayList<? extends PacGameGrid> models) {
        setUpProperties();
        for (int i = 0; i < models.size(); i++) {
            boolean isLastGame = false;
            if (i == models.size() - 1) {
                isLastGame = true;
            }
            Game game = new Game(this, models.get(i), isLastGame);
            games.add(game);
        }
    }

    private void setUpProperties() {
        Properties properties = PropertiesLoader.loadPropertiesFile(defaultPropertiesPath);
        seed = Integer.parseInt(properties.getProperty("seed"));
        isPacActorAuto = Boolean.parseBoolean(properties.getProperty("PacMan.isAuto"));
    }

    public void runGames() {
        for (Game game : games) {
            if (!game.runGame()) {
                return;
            }
        }
    }

    public int getSeed() {
        return seed;
    }

    public boolean isPacActorAuto() {
        return isPacActorAuto;
    }

    public GameCallback getGameCallback() {
        return gameCallback;
    }
}
