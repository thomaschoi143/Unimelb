import bagel.*;

/**
 * Project2B for SWEN20003 Project 2, Semester 1, 2022
 * Shadow Pirate game main program
 *
 * @author Thomas Choi
 * @version 1.0
 */
public class ShadowPirate extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "ShadowPirate";

    private final Level0 level0 = new Level0();
    private final Level1 level1 = new Level1();
    private boolean isInLevel0 = true;

    /**
     * Method that constructs a ShadowPirate
     */
    public ShadowPirate() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
    }

    /**
     * The main method that is the entry point for the program.
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        ShadowPirate game = new ShadowPirate();
        game.run();
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     * @param input The input from the user
     */
    @Override
    public void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        if (isInLevel0) {
            isInLevel0 = level0.update(input);
        } else {
            level1.update(input);
        }

    }

}
