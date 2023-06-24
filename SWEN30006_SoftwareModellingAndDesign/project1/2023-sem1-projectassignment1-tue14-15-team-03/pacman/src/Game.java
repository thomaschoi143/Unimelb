/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
// Simple version of the game
package src;

import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game extends GameGrid {
    private final static int cellSize = 20;
    private final static boolean noNavigation = false;
    private final static int simulationPeriod = 100;
    private final static int simulationCycleDelay = 10;
    private final static int endGameDelay = 120;
    private final static int keyRepeatPeriod = 150;
    private PacActor pacActor;
    protected final ArrayList<Monster> monsters = new ArrayList<>();
    protected final ArrayList<Item> items = new ArrayList<>();
    private PacManGameGrid grid = new PacManGameGrid();
    private GameCallback gameCallback = new GameCallback();
    private BackgroundHandler backgroundHandler = new BackgroundHandler(getBg());

    public Game(int seed, HashMap<String, ArrayList<Location>> actorLocationsMap,
                List<String> propertyMoves, boolean isPacActorAuto) {
        // Setup game
        super(PacManGameGrid.nbHorzCells, PacManGameGrid.nbVertCells, cellSize, noNavigation);
        setSimulationPeriod(simulationPeriod);
        setTitle("[PacMan in the Multiverse]");

        // Item
        setUpItems(actorLocationsMap);

        // Monsters
        addMonster(new TX5(this, seed), actorLocationsMap.get("TX5").get(0));
        addMonster(new Troll(this, seed), actorLocationsMap.get("Troll").get(0));

        // PacActor
        if (isPacActorAuto) {
            pacActor = new PacActorAuto(this, seed, propertyMoves, getItemsLocation());
        } else {
            pacActor = new PacActorManual(this, seed);
            addKeyRepeatListener((PacActorManual)pacActor);
            setKeyRepeatPeriod(keyRepeatPeriod);
        }
        addActor(pacActor, actorLocationsMap.get("PacMan").get(0));
    }

    // Start the game
    public void runGame() {
        doRun();
        show();
        // Loop to look for collision in the application thread
        // This makes it improbable that we miss a hit
        boolean hasPacmanBeenHit = false;
        boolean hasPacmanEatAllPills;
        int pillsAndGoldCount = getPillsAndGoldCount();
        Location pacActorLocation;

        do {
            pacActorLocation = pacActor.getLocation();

            // Check if the pacActor collides with any monsters (losing condition)
            for (Monster monster : monsters) {
                if (monster.getLocation().equals(pacActorLocation)) {
                hasPacmanBeenHit = true;
                break;
              }
            }

            // Check the win condition
            hasPacmanEatAllPills = pacActor.getNbPillsAndGold() == pillsAndGoldCount;

            delay(simulationCycleDelay);
        } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
        delay(endGameDelay);

        endGame(hasPacmanBeenHit, hasPacmanEatAllPills);
    }

    // End the game
    private void endGame(boolean hasPacmanBeenHit, boolean hasPacmanEatAllPills) {
        Location loc = pacActor.getLocation();
        pacActor.removeSelf();

        for (Monster monster : monsters) {
            monster.setState(MonsterState.FROZEN);
        }

        String title = "";
        if (hasPacmanBeenHit) {
            title = "GAME OVER";
            addActor(new Explosion(), loc);
        } else if (hasPacmanEatAllPills) {
            title = "YOU WIN";
        }
        setTitle(title);
        gameCallback.endOfGame(title);

        doPause();
    }

    // Put items in the game according to the properties and the default meze
    private void setUpItems(HashMap<String, ArrayList<Location>> actorLocationsMap) {
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                Location location = new Location(x, y);
                GridElementType type = grid.getDefaultCell(location);

                if (type != GridElementType.UNDEFINED && type != GridElementType.BLOCK) {
                    backgroundHandler.drawSpace(location);

                    if (type == GridElementType.PILL && actorLocationsMap.get("Pills").size() == 0) {
                        putItem(location, new Pill());
                    } else if (type == GridElementType.GOLD && actorLocationsMap.get("Gold").size() == 0) {
                        putItem(location, new Gold());
                    } else if (type == GridElementType.ICE) {
                        putItem(location, new Ice());
                    }
                }

            }
        }

        for (Location location : actorLocationsMap.get("Pills")) {
            putItem(location, new Pill());
        }

        for (Location location : actorLocationsMap.get("Gold")) {
            putItem(location, new Gold());
        }
    }

    // Add the monster to the game
    protected void addMonster(Monster monster, Location location) {
        monsters.add(monster);
        addActor(monster, location, Monster.monsterDefaultDirection);
    }

    // Add the item to the game
    private void putItem(Location location, Item item) {
        backgroundHandler.drawItem(item.getColor(), toPoint(location));
        items.add(item);
        addActor(item, location);
    }

    // Check if the pacActor eats any items
    public void checkEatItem() {
        Location pacActorLocation = pacActor.getLocation();
        if (backgroundHandler.isItemLocation(pacActorLocation)) {
            for (Item item : items) {
                if (item.getLocation().equals(pacActorLocation)) {
                    itemIsEaten(pacActorLocation, item);
                    break; // Assume items cannot overlap
                }
            }
        }
    }

    // Hide the item and add the score to pacMan if an item is eaten
    protected void itemIsEaten(Location location, Item item) {
        backgroundHandler.drawSpace(location);
        item.hide();
        pacActor.addScore(item.getEarningScore());
        if (item instanceof Pill) {
            pacActor.addNbPillsAndGold();
            gameCallback.pacManEatPillsAndItems(location, "pills");
        } else if (item instanceof Gold) {
            pacActor.addNbPillsAndGold();
            gameCallback.pacManEatPillsAndItems(location, "gold");
        } else {
            gameCallback.pacManEatPillsAndItems(location, "ice");
        }
        String title = "[PacMan in the Multiverse] Current score: " + pacActor.getScore();
        setTitle(title);
    }

    private int getPillsAndGoldCount() {
        int count = 0;
        for (Item item : items) {
            if (item instanceof Pill || item instanceof Gold) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Location> getItemsLocation() {
        ArrayList<Location> locations = new ArrayList<>();
        for (Item item : items) {
            locations.add(item.getLocation());
        }
        return locations;
    }

    public GameCallback getGameCallback() {
      return gameCallback;
    }

    public BackgroundHandler getBackgroundHandler() {
      return backgroundHandler;
    }

    public Location getPacActorLocation() {
      return pacActor.getLocation();
    }


}


