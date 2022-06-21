import bagel.util.Rectangle;
import java.util.ArrayList;

/**
 * Class that inherits from AbstractLevel and provides a template for level 1
 * @author Thomas Choi
 * @version 1.0
 */
public class Level1 extends AbstractLevel{
    private final static String GOAL = "FIND THE TREASURE";
    private final static String WON_MESSAGE = "CONGRATULATIONS!";
    private final static String BACKGROUND_FILENAME = "res/background1.png";
    private final static String INPUT_FILE = "res/level1.csv";

    // Level-specific game entities
    private final ArrayList<Item> items = new ArrayList<Item>();
    private Treasure treasure;

    /**
     * Method that constructs Level1
     */
    public Level1() {
        super(BACKGROUND_FILENAME);
    }

    // Method that performs the state update for level-specific game entities
    @Override
    protected void runningStage() {
        for (int i = 0; i < getNonOverlapables().size(); i++) {
            Bomb bomb = (Bomb)getNonOverlapables().get(i);
            if (!bomb.update()) {
                // The bomb has exploded
                getNonOverlapables().remove(i);
                // As all other bombs after this removed bomb has moved forward by 1 index
                i--;
            }
        }

        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).update((SailorLevel1) getSailor())) {
                // The item has been picked up
                items.remove(i);
                // As all other items after this removed item has moved forward by 1 index
                i--;
            }
        }

        treasure.render();
    }

    // Method that reads level-specific game entities
    @Override
    protected void readLevelSpecific(String type, int x, int y) {
        switch (type) {
            case "Block":
                getNonOverlapables().add(new Bomb(x, y));
                break;
            case "Sailor":
                setSailor(new SailorLevel1(x, y));
                break;
            case "Potion":
                items.add(new Potion(x, y));
                break;
            case "Elixir":
                items.add(new Elixir(x, y));
                break;
            case "Sword":
                items.add(new Sword(x, y));
                break;
            case "Treasure":
                treasure = new Treasure(x, y);
                break;
            case "Blackbeard":
                getEnemies().add(new Blackbeard(x, y));
                break;
        }
    }

    // Method that checks the level-specific win condition
    @Override
    protected boolean hasWon() {
        Sailor sailor = getSailor();
        Rectangle sailorRec = new Rectangle(sailor.getX(), sailor.getY(), sailor.getCurrentImage().getWidth(),
                sailor.getCurrentImage().getHeight());
        Rectangle treasureRec = new Rectangle(treasure.getX(), treasure.getY(), treasure.getCurrentImage().getWidth(),
                treasure.getCurrentImage().getHeight());

        return treasureRec.intersects(sailorRec);
    }

    // Method that performs the state update of the win stage
    @Override
    protected boolean wonStage() {
        getMESSAGE().drawMessage(WON_MESSAGE, NO_OFFSET);
        return true;
    }

    // Getters
    @Override
    protected String getGoal() {
        return GOAL;
    }

    @Override
    protected  String getInputFile() {
        return INPUT_FILE;
    }

}
