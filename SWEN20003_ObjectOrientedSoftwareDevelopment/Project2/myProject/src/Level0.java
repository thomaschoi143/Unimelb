import bagel.util.Point;

/**
 * Class that inherits from AbstractLevel and provides a template for level 0
 * @author Thomas Choi
 * @version 1.3
 */
public class Level0 extends AbstractLevel{
    private final static String GOAL = "USE ARROW KEYS TO FIND LADDER";
    private final static String WON_MESSAGE = "LEVEL COMPLETE!";
    private final static String INPUT_FILE = "res/level0.csv";
    private final static String BACKGROUND_FILENAME = "res/background0.png";
    private final static int LEVEL_COMPETE_TIME_MS = 3000;
    private final static Point LADDER = new Point(990, 630);

    private int levelCompeteMessageTimeCounter = 0;

    /**
     * Method that constructs Level0
     */
    public Level0() {
        super(BACKGROUND_FILENAME);
    }

    // Method that performs the state update for level-specific game entities
    @Override
    protected void runningStage() {
        for (NonOverlapable nonOverlapable : getNonOverlapables()) {
            ((Block)nonOverlapable).render();
        }
    }

    // Method that reads level-specific game entities
    @Override
    protected void readLevelSpecific(String type, int x, int y) {
        switch(type) {
            case "Block":
                getNonOverlapables().add(new Block(x, y));
                break;
            case "Sailor":
                setSailor(new Sailor(x, y));
                break;
        }
    }

    //  Method that determine the level-specific win condition
    @Override
    protected boolean hasWon() {
        return (getSailor().getX() >= LADDER.x && getSailor().getY() > LADDER.y);
    }

    //  Method that performs the state update of the win stage
    @Override
    protected boolean wonStage() {
        getMESSAGE().drawMessage(WON_MESSAGE, NO_OFFSET);
        levelCompeteMessageTimeCounter++;

        // Show the won message for LEVEL_COMPETE_TIME_MS
        return (levelCompeteMessageTimeCounter / (REFRESH_RATE / 1000.0) != LEVEL_COMPETE_TIME_MS);
    }

    // Getters
    @Override
    protected String getGoal() {
        return GOAL;
    }

    @Override
    protected String getInputFile() {
        return INPUT_FILE;
    }

}
