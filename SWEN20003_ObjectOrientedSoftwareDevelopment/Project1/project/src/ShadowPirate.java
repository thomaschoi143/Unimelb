import bagel.*;
import bagel.util.Point;
import bagel.util.Colour;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 1, 2022
 *
 * Please filling your name below
 * @author Thomas Choi
 */
public class ShadowPirate extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static int MAX_BLOCKS = 49;
    private final static String GAME_TITLE = "ShadowPirate";
    private final static String INPUT_FILE = "res/level0.csv";

    // Message drawing constants
    private final static int MESSAGE_SIZE = 55;
    private final static double MESSAGE_Y = 402;
    private final static int GAME_GOAL_SEPARATE = 70;

    // Health points drawing constants
    private final static int HEALTH_POINTS_SIZE = 30;
    private final static Point HEALTH_POINTS_POINT = new Point(10, 25);
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    // Game status constants
    private final static int NOT_START = 0;
    private final static int RUNNING = 1;
    private final static int WON = 2;
    private final static int LOST = 3;

    // Win or lose checking constants
    private final static Point LADDER = new Point(990, 630);
    private final static int TOP_BOUND = 60;
    private final static int BOTTOM_BOUND = 670;
    private final static int LEFT_BOUND = 0;
    private final static int LOSE_POINT = 0;

    // Fixed shown String
    private final static String INSTRUCTION_STR = "PRESS SPACE TO START";
    private final static String GOAL_STR = "USE ARROW KEYS TO FIND LADDER";
    private final static String WIN_STR = "CONGRATULATIONS!";
    private final static String LOSE_STR = "GAME OVER";

    // Image and Font
    private final Image BACKGROUND_IMAGE = new Image("res/background0.png");
    private final Font MESSAGE = new Font("res/wheaton.otf", MESSAGE_SIZE);
    private final Font HEALTH_POINTS = new Font("res/wheaton.otf", HEALTH_POINTS_SIZE);

    // Instance variables
    private int gameStatus = NOT_START;
    private Sailor sailor;
    private final Block[] blocks = new Block[MAX_BLOCKS];
    private int numBlocks = 0;

    // Constructor
    public ShadowPirate() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowPirate game = new ShadowPirate();
        game.readCSV(INPUT_FILE);
        game.run();
    }

    /**
     * Method used to read file and create objects
     */
    private void readCSV(String fileName){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String text;
            boolean hadReadSailor = false;

            while ((text = br.readLine()) != null) {
                String[] cells = text.split(",");

                // Assume the first entry is always the sailor
                if (!hadReadSailor) {
                    sailor = new Sailor(Integer.parseInt(cells[1]), Integer.parseInt(cells[2]));
                    hadReadSailor = true;
                } else {
                    blocks[numBlocks] = new Block(Integer.parseInt(cells[1]), Integer.parseInt(cells[2]));
                    numBlocks++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---- Belows are all the drawing methods ---- */
    /**
     * A general overloading method to draw a message
     */
    private void drawMessage(String str) {
        MESSAGE.drawString(str, Window.getWidth()/2.0 - MESSAGE.getWidth(str)/2.0, MESSAGE_Y);
    }

    private void drawMessage(String str, double separate) {
        MESSAGE.drawString(str, Window.getWidth()/2.0 - MESSAGE.getWidth(str)/2.0, MESSAGE_Y + separate);
    }

    /**
     * Display the health points percentage in the top left corner
     */
    private void drawHealthPoints() {
        double percentage = (double) sailor.getHealthPoints() / Sailor.MAX_HEALTH_POINTS * 100;
        Colour colour = GREEN;

        if (percentage < 35) {
            colour = RED;
        } else if (percentage < 65) {
            colour = ORANGE;
        }

        HEALTH_POINTS.drawString(String.format("%.0f%%", percentage), HEALTH_POINTS_POINT.x, HEALTH_POINTS_POINT.y,
                new DrawOptions().setBlendColour(colour));
    }

    /**
     * Draw all the stationary blocks in the game
     */
    private void drawBlocks() {
        for(int i = 0; i < numBlocks; i++) {
            blocks[i].getIMAGE().draw(blocks[i].getX(), blocks[i].getY());
        }
    }

    /* ---- Aboves are all the drawing methods ---- */

    /* ---- Belows are all the checking win or lose methods ---- */
    /**
     * Check the win condition
     */
    private void checkWin() {
        if (sailor.getX() >= LADDER.x && sailor.getY() > LADDER.y) {
            gameStatus = WON;
        }
    }

    /**
     * Check the out-of-bound condition
     */
    private void checkOutOfBound() {
        final int RIGHT_BOUND = Window.getWidth();
        if (sailor.getY() < TOP_BOUND || sailor.getY() > BOTTOM_BOUND || sailor.getX() < LEFT_BOUND ||
                sailor.getX() > RIGHT_BOUND) {
            gameStatus = LOST;
        }
    }

    /**
     * Check the lose condition
     */
    private void checkLose() {
        if (sailor.getHealthPoints() <= LOSE_POINT) {
            gameStatus = LOST;
        }
    }

    /* ---- Aboves are all the checking win or lose methods ---- */

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {
        BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        } else if (gameStatus == NOT_START) {
            drawMessage(INSTRUCTION_STR);
            drawMessage(GOAL_STR, GAME_GOAL_SEPARATE);

            if (input.wasPressed(Keys.SPACE)) {
                gameStatus = RUNNING;
            }
        } else if (gameStatus == RUNNING) {
            int numAttacks = 0;

            drawHealthPoints();
            drawBlocks();

            sailor.move(input);
            sailor.getImage().draw(sailor.getX(), sailor.getY());

            // Count how many attacks by blocks
            for(int i = 0; i < numBlocks; i++) {
                numAttacks += blocks[i].attack(sailor);
            }
            /* As there may be more than one attacks by different blocks,
             * only rebounce after all the damage points have deducted
             */
            if (numAttacks > 0) {
                sailor.rebounce(input);
            }

            checkWin();
            checkOutOfBound();
            checkLose();

        } else if (gameStatus == WON) {
            drawMessage(WIN_STR);
        } else {
            // Player has lost
            drawMessage(LOSE_STR);
        }

    }

}
