/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
// Extended version of the game

package src;

import ch.aplu.jgamegrid.Location;
import src.utility.GameCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameExtended extends Game{

    public GameExtended(int seed, HashMap<String, ArrayList<Location>> actorLocationsMap,
                        List<String> propertyMoves, boolean isPacActorAuto) {
        super(seed, actorLocationsMap, propertyMoves, isPacActorAuto);

        // Monster
        addMonster(new Alien(this, seed), actorLocationsMap.get("Alien").get(0));
        addMonster(new Wizard(this, seed), actorLocationsMap.get("Wizard").get(0));
        addMonster(new Orion(this, seed, getGolds()), actorLocationsMap.get("Orion").get(0));
    }

    // Perform the effect of Gold and Ice in the extended version
    protected void itemIsEaten(Location location, Item item) {
        super.itemIsEaten(location, item);
        if (item instanceof Gold) {
            for (Monster monster : monsters) {
                monster.walkFuriously();
            }
        } else if (item instanceof Ice) {
            for (Monster monster: monsters) {
                monster.freeze(monster.getFrozenSeconds());
            }
        }
    }

    public ArrayList<Gold> getGolds() {
        ArrayList<Gold> golds = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof Gold) {
                golds.add((Gold)item);
            }
        }
        return golds;
    }
}
