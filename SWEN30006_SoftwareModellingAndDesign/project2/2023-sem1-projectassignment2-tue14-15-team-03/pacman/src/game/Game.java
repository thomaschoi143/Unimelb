/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
// Simple version of the game
package src.game;

import ch.aplu.jgamegrid.*;
import src.game.item.*;
import src.game.monster.*;
import src.game.pacActor.PacActor;
import src.game.pacActor.PacActorAuto;
import src.game.pacActor.PacActorManual;
import src.game.portal.*;
import src.gameGrid.GridElementType;
import src.gameGrid.PacGameGrid;
import src.utility.GameCallback;

import java.util.ArrayList;

import static ch.aplu.util.QuitPane.dispose;


public class Game extends GameGrid {
    private final static int cellSize = 20;
    private final static boolean noNavigation = false;
    private final static int simulationPeriod = 100;
    private final static int simulationCycleDelay = 10;
    private final static int endGameDelay = 120;
    private final static int keyRepeatPeriod = 150;
    private final static int closeGameDelay = 1000;
    private PacActor pacActor;
    private final ArrayList<Monster> monsters = new ArrayList<>();
    private final ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<Portal> portals = new ArrayList<>();
    private BackgroundHandler backgroundHandler;
    private PacGameGrid model;
    private GameFacade gameFacade;
    private boolean isLastGame;

    public Game(GameFacade gameFacade, PacGameGrid model, boolean isLastGame) {
        // Setup game
        super(model.getWidth(), model.getHeight(), cellSize, noNavigation);
        this.model = model;
        this.gameFacade = gameFacade;
        this.isLastGame = isLastGame;
        setSimulationPeriod(simulationPeriod);
        setTitle("[PacMan in the TorusVerse]");
        backgroundHandler = new BackgroundHandler(getBg());

        if (gameFacade.isPacActorAuto()) {
            pacActor = new PacActorAuto(this, gameFacade.getSeed());
        } else {
            pacActor = new PacActorManual(this, gameFacade.getSeed());
            addKeyRepeatListener((PacActorManual)pacActor);
            setKeyRepeatPeriod(keyRepeatPeriod);
        }

        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                Location location = new Location(x, y);
                GridElementType type = GridElementType.charToGridElementType(model.getTile(x, y));
                if (type != GridElementType.WallTile && type != GridElementType.Undefined) {
                    backgroundHandler.drawSpace(location);
                    switch (type) {
                        case PillTile -> putItem(location, PillBuilder.getInstance().buildItem());
                        case GoldTile -> putItem(location, GoldBuilder.getInstance().buildItem());
                        case IceTile -> putItem(location, IceBuilder.getInstance().buildItem());
                        case PacTile -> addActor(pacActor, location);
                        case TrollTile -> addMonster(new Troll(this, gameFacade.getSeed()), location);
                        case TX5Tile ->  addMonster(new TX5(this, gameFacade.getSeed()), location);
                        case PortalWhiteTile -> addPortal(location, WhitePortalBuilder.getInstance().buildPortal());
                        case PortalYellowTile -> addPortal(location, YellowPortalBuilder.getInstance().buildPortal());
                        case PortalDarkGoldTile -> addPortal(location, DarkGoldPortalBuilder.getInstance().buildPortal());
                        case PortalDarkGrayTile -> addPortal(location, DarkGrayPortalBuilder.getInstance().buildPortal());
                    }
                }
            }
        }

        // Arrange the paint order
        ArrayList<Class> paintOrder = new ArrayList<>();
        for (Monster monster : monsters) {
            paintOrder.add(monster.getClass());
        }
        paintOrder.add(pacActor.getClass());
        for (Portal portal : portals) {
            paintOrder.add(portal.getClass());
        }
        for (Item item : items) {
            paintOrder.add(item.getClass());
        }
        setPaintOrder(paintOrder.toArray(new Class[paintOrder.size()]));
    }

    // Start the game
    public boolean runGame() {
        show();
        doRun();

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
            checkEatItem();
            checkPortalEntry();

            // Check the win condition
            hasPacmanEatAllPills = pacActor.getNbPillsAndGold() == pillsAndGoldCount;

            delay(simulationCycleDelay);
        } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
        delay(endGameDelay);

        return endGame(hasPacmanBeenHit, hasPacmanEatAllPills);
    }

    // End the game
    private boolean endGame(boolean hasPacmanBeenHit, boolean hasPacmanEatAllPills) {
        boolean isWin = true;
        doPause();
        for (Monster monster : monsters) {
            monster.setState(MonsterState.FROZEN);
        }
        Location loc = pacActor.getLocation();
        pacActor.removeSelf();

        String title;
        if (hasPacmanBeenHit) {
            title = "GAME OVER";
            addActor(new Explosion(), loc);
            isWin = false;
            setTitle(title);
            gameFacade.getGameCallback().endOfGame(title);
        } else if (hasPacmanEatAllPills && isLastGame) {
            title = "YOU WIN";
            setTitle(title);
            gameFacade.getGameCallback().endOfGame(title);
        }

        delay(closeGameDelay);
        hide();
        dispose();

        return isWin;
    }

    // Add the monster to the game
    private void addMonster(Monster monster, Location location) {
        monsters.add(monster);
        addActor(monster, location, Monster.monsterDefaultDirection);
    }

    // Add the item to the game
    private void putItem(Location location, Item item) {
        backgroundHandler.drawItem(item.getColor(), toPoint(location));
        items.add(item);
        addActor(item, location);
    }

    // Add the portal to the game
    private void addPortal(Location location, Portal portal) {
        for (Portal portalEntity : portals) {
            if (portalEntity.getType().equals(portal.getType())) {
                portalEntity.setTwinPortal(portal);
                portal.setTwinPortal(portalEntity);
                break;
            }
        }

        backgroundHandler.drawPortal(location);
        portals.add(portal);
        addActor(portal, location);
    }

    // Check if PacMan lands on a portal location
    private void checkPortalEntry() {
        Location pacActorLocation = pacActor.getLocation();

        // Check if a moving actor lands on a Portal and enter if portal is not active
        for (Portal portal : portals) {
            if (pacActorLocation.equals(portal.getLocation()) && !portal.isActive()) {
                portal.setActive(true);
                enterPortal(portal, pacActor);
                break;
            }

            for (Monster monster : monsters) {
                if (monster.getLocation().equals(portal.getLocation()) && !portal.isActive()) {
                    portal.setActive(true);
                    enterPortal(portal, monster);
                    break;
                }
            }
        }

        //Check if the portals currently have a moving actor on it or not, if it doesn't, then make it inactive
        for (Portal portal : portals) {
            boolean occupied = false;
            if (pacActorLocation.equals(portal.getLocation())) {
                occupied = true;
            }

            for(Monster monster : monsters){
                if(monster.getLocation().equals(portal.getLocation())){
                    occupied = true;
                    break;
                }
            }

            if (!occupied) {
                portal.setActive(false);
            }

        }
    }
    // Enter the portal and teleport to its twin portal
    private void enterPortal(Portal portal, MovingActor actorEntering){
        // If twin portal is not active, the moving actor can travel through it
        if (!portal.getTwinPortal().isActive()) {
            actorEntering.setLocation(portal.getTwinPortal().getLocation());
            portal.getTwinPortal().setActive(true);
        }
    }

    // Check if the pacActor eats any items
    private void checkEatItem() {
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
    private void itemIsEaten(Location location, Item item) {
        backgroundHandler.drawSpace(location);
        item.hide();
        pacActor.addScore(item.getEarningScore());
        if (item.getType() == ItemType.PILL) {
            pacActor.addNbPillsAndGold();
            gameFacade.getGameCallback().pacManEatPillsAndItems(location, "pills");
        } else if (item.getType() == ItemType.GOLD) {
            pacActor.addNbPillsAndGold();
            gameFacade.getGameCallback().pacManEatPillsAndItems(location, "gold");
        } else {
            gameFacade.getGameCallback().pacManEatPillsAndItems(location, "ice");
        }
        String title = "[PacMan in the TorusVerse] Current score: " + pacActor.getScore();
        setTitle(title);
    }

    private int getPillsAndGoldCount() {
        int count = 0;
        for (Item item : items) {
            if (item.getType() == ItemType.PILL || item.getType() == ItemType.GOLD) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Item> getPillsAndGolds() {
        ArrayList<Item> pillsAndGolds = new ArrayList<>();
        for (Item item : items) {
            if (item.getType() == ItemType.PILL || item.getType() == ItemType.GOLD) {
                pillsAndGolds.add(item);
            }
        }
        return pillsAndGolds;
    }

    public BackgroundHandler getBackgroundHandler() {
      return backgroundHandler;
    }

    public PacActor getPacActor() {
        return pacActor;
    }

    public PacGameGrid getModel() {
        return model;
    }

    public GameCallback getGameCallback() {
        return gameFacade.getGameCallback();
    }
}


