/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

// Controller of Game

package src;

import ch.aplu.jgamegrid.Location;
import src.utility.PropertiesLoader;

import java.util.*;

public class GameHandler {
    private final static ArrayList<String> actorsName = new ArrayList<>(Arrays.asList("TX5", "Troll", "PacMan", "Orion",
            "Alien", "Wizard", "Pills", "Gold"));
    private Game game;

    // Parse the input properties and create the Game
    public void createGame(String propertiesPath) {
        List<String> propertyMoves = null;
        Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);

        boolean isSimple = properties.getProperty("version").equals("simple");
        int seed = Integer.parseInt(properties.getProperty("seed"));
        String propertyMovesString = properties.getProperty("PacMan.move");
        if (propertyMovesString != null) {
            propertyMoves = Arrays.asList(propertyMovesString.split(","));
        }
        boolean isPacActorAuto = Boolean.parseBoolean(properties.getProperty("PacMan.isAuto"));
        HashMap<String, ArrayList<Location>> actorLocationsMap = parseAllLocations(properties);

        if (isSimple) {
            game = new Game(seed, actorLocationsMap, propertyMoves, isPacActorAuto);
        } else {
            game = new GameExtended(seed, actorLocationsMap, propertyMoves, isPacActorAuto);
        }
    }

    public void runGame() {
        game.runGame();
    }

    // Parse all locations of actors to a HashMap storing the name of the actor and its Locations
    private HashMap<String, ArrayList<Location>> parseAllLocations(Properties properties) {
        HashMap<String, ArrayList<Location>> actorLocationsMap = new HashMap<>();
        for (String name : actorsName) {
            ArrayList<Location> locations = new ArrayList<>();
            String propertyString = properties.getProperty(name + ".location");
            if (propertyString != null) {
                String[] locationStrings = propertyString.split(";");
                for (String locationString : locationStrings) {
                    locations.add(parseLocationString(locationString));
                }
            }
            actorLocationsMap.put(name, locations);
        }
        return actorLocationsMap;
    }

    // Parse location string "x,y" to Location
    private Location parseLocationString(String str) {
        String[] locationStrings = str.split(",");
        int[] locationCoord = new int[locationStrings.length];
        for (int i = 0; i < locationStrings.length; i++) {
            locationCoord[i] = Integer.parseInt(locationStrings[i]);
        }
        return new Location(locationCoord[0], locationCoord[1]);
    }


}
