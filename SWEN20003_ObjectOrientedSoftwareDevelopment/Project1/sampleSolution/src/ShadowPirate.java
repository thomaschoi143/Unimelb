import bagel.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * SWEN20003 Project 1, Semester 1, 2022
 *
 * @author Tharun Dharmawickrema
 */
public class ShadowPirate extends AbstractGame{
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "ShadowPirate";
    private final Image BACKGROUND_IMAGE = new Image("res/background0.png");
    private final static String WORLD_FILE = "res/level0.csv";
    private final static String START_MESSAGE = "PRESS SPACE TO START";
    private final static String INSTRUCTION_MESSAGE = "USE ARROW KEYS TO FIND LADDER";
    private final static String END_MESSAGE = "GAME OVER";
    private final static String WIN_MESSAGE = "CONGRATULATIONS!";

    private final static int INSTRUCTION_OFFSET = 70;
    private final static int FONT_SIZE = 55;
    private final static int FONT_Y_POS = 402;
    private final Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    private final static int MAX_ARRAY_SIZE = 49;
    private final static Block[] blocks = new Block[MAX_ARRAY_SIZE];

    private Sailor sailor;
    private boolean gameOn;
    private boolean gameEnd;
    private boolean gameWin;

    public ShadowPirate(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        readCSV(WORLD_FILE);
        gameWin = false;
        gameEnd = false;
        gameOn = false;
    }

    /**
     * Entry point for program
     */
    public static void main(String[] args){
        ShadowPirate game = new ShadowPirate();
        game.run();
    }

    /**
     * Method used to read file and create objects
     */
    private void readCSV(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){

            String line;
            if ((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                if (sections[0].equals("Sailor")){
                    sailor = new Sailor(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                }
            }

            int current = 0;
            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                if (sections[0].equals("Block")){
                    blocks[current] = new Block(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                    current++;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Performs a state update. Pressing escape key,
     * allows game to exit.
     */
    @Override
    public void update(Input input){
        BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        if (!gameOn){
            drawStartScreen(input);
        }

        if (gameEnd || sailor.isOutOfBound()){
            gameEnd = true;
            drawEndScreen(END_MESSAGE);
        }

        if (gameWin){
            drawEndScreen(WIN_MESSAGE);
        }

        // when game is running
        if (gameOn && !gameEnd && !gameWin){
            for (Block block : blocks) {
                block.update();
            }
            sailor.update(input, blocks);

            if (sailor.isDead()){
                gameEnd = true;
            }

            if (sailor.hasWon()){
                gameWin = true;
            }
        }
    }

    /**
     * Method used to draw the start screen instructions
     */
    private void drawStartScreen(Input input){
        FONT.drawString(START_MESSAGE, (Window.getWidth()/2.0 - (FONT.getWidth(START_MESSAGE)/2.0)),
                FONT_Y_POS);
        FONT.drawString(INSTRUCTION_MESSAGE, (Window.getWidth()/2.0 - (FONT.getWidth(INSTRUCTION_MESSAGE)/2.0)),
                (FONT_Y_POS + INSTRUCTION_OFFSET));
        if (input.wasPressed(Keys.SPACE)){
            gameOn = true;
        }
    }

    /**
     * Method used to draw end screen messages
     */
    private void drawEndScreen(String message){
        FONT.drawString(message, (Window.getWidth()/2.0 - (FONT.getWidth(message)/2.0)), FONT_Y_POS);
    }
}