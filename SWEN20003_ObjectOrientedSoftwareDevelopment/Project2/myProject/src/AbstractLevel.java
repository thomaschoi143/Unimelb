import bagel.*;
import bagel.util.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Abstract class that provides template for a game level
 * @author Thomas Choi
 * @version 1.0
 */
public abstract class AbstractLevel {
    private final static String[] INSTRUCTIONS = new String[]{"PRESS SPACE TO START", "PRESS S TO ATTACK"};
    private final static int MESSAGE_OFFSET = 70;
    protected final static int NO_OFFSET = 0;
    protected final static int REFRESH_RATE = 60;
    private final static String LOST_MESSAGE = "GAME OVER";

    // Game status constants
    private final static int NOT_START = 0;
    private final static int RUNNING = 1;
    private final static int WON = 2;
    private final static int LOSE = 3;

    // Game entities
    private final ArrayList<NonOverlapable> nonOverlapables = new ArrayList<NonOverlapable>();
    private final ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private final ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    private Sailor sailor;

    private Point topLeftBound, bottomRightBound;
    private boolean haveReadCSV = false;
    private int gameStatus = NOT_START;
    private final Message MESSAGE = new Message();
    private final Image BACKGROUND_IMAGE;

    /**
     * Method that constructs an AbstractLevel
     * @param backgroundFileName This is the filename of the background of the level
     */
    public AbstractLevel(String backgroundFileName) {
        BACKGROUND_IMAGE = new Image(backgroundFileName);
    }

    /**
     * Method that performs state update
     * @param input This is the input from user
     * @return boolean This returns whether the level has ended
     */
    public boolean update(Input input) {
        int i;
        switch (gameStatus) {
            case NOT_START:
                if (!haveReadCSV) {
                    readCSV(getInputFile());
                    haveReadCSV = true;
                }

                for (i = 0; i < INSTRUCTIONS.length; i++) {
                    // The first line of the message has (-1 * MESSAGE_OFFSET) for the y offset
                    MESSAGE.drawMessage(INSTRUCTIONS[i], (i - 1) * MESSAGE_OFFSET);
                }
                MESSAGE.drawMessage(getGoal(), (i - 1) * MESSAGE_OFFSET);

                if (input.wasPressed(Keys.SPACE)) {
                    gameStatus = RUNNING;
                }

                // For testing
                if (input.wasPressed(Keys.W)) {
                    return false;
                }
                break;
            case RUNNING:
                BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

                // Level-specific part
                runningStage();

                // Enemies part
                for (i = 0; i < enemies.size(); i++) {
                    if (!enemies.get(i).update(nonOverlapables, sailor, projectiles, topLeftBound, bottomRightBound)) {
                        // The enemy has died
                        enemies.remove(i);
                        // As all enemies after this removed enemy has moved forward by 1 index
                        i--;
                    }
                }

                // Projectile part
                for (i = 0; i < projectiles.size(); i++) {
                    if (!projectiles.get(i).update(sailor, topLeftBound, bottomRightBound)) {
                        // The projectile has died
                        projectiles.remove(i);
                        // As all other projectiles after this removed projectile has moved forward by 1 index
                        i--;
                    }
                }

                // Sailor part
                sailor.update(input, nonOverlapables, enemies, topLeftBound, bottomRightBound);

                if (hasWon()) {
                    gameStatus = WON;
                }

                if (hasLost()) {
                    gameStatus = LOSE;
                }
                break;
            case WON:
                return wonStage();
            case LOSE:
                MESSAGE.drawMessage(LOST_MESSAGE, NO_OFFSET);
                break;
        }
        return true;
    }

    // Method used to read file and create objects
    private void readCSV(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String text;

            while ((text = br.readLine()) != null) {
                String[] cells = text.split(",");
                String type = cells[0];

                switch (type) {
                    case "Pirate":
                        enemies.add(new Pirate(Integer.parseInt(cells[1]), Integer.parseInt(cells[2])));
                        break;
                    case "TopLeft":
                        topLeftBound = new Point(Integer.parseInt(cells[1]), Integer.parseInt(cells[2]));
                        break;
                    case "BottomRight":
                        bottomRightBound = new Point(Integer.parseInt(cells[1]), Integer.parseInt(cells[2]));
                        break;
                    default:
                        // Read level-specific game entities
                        readLevelSpecific(type, Integer.parseInt(cells[1]), Integer.parseInt(cells[2]));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method that determine the loss condition of all levels
    private boolean hasLost() {
        return (sailor.getHealthPoints() <= 0);
    }

    // Abstract methods
    protected abstract void readLevelSpecific(String type, int x, int y);
    protected abstract String getInputFile();
    protected abstract String getGoal();
    protected abstract boolean wonStage();
    protected abstract void runningStage();
    protected abstract boolean hasWon();

    // Getters and setters
    /**
     * Method that gets nonOverlapables
     * @return ArrayList<NonOverlapable> This returns nonOverlapables array list
     */
    public ArrayList<NonOverlapable> getNonOverlapables() {
        return nonOverlapables;
    }

    /**
     * Method that gets enemies
     * @return ArrayList<Enemy> This returns enemies array list
     */
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Method that gets sailor
     * @return Sailor This returns sailor
     */
    public Sailor getSailor() {
        return sailor;
    }

    /**
     * Method that gets MESSAGE
     * @return Message This returns MESSAGE
     */
    public Message getMESSAGE() {
        return MESSAGE;
    }

    /**
     * Method that sets the level's sailor
     * @param sailor Sailor that to be set
     */
    public void setSailor(Sailor sailor) {
        this.sailor = sailor;
    }

}
