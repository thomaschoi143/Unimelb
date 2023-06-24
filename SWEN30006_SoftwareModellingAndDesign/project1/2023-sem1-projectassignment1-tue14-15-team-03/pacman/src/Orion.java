/*
* Team03
* Member: Thomas Choi
*         Lucas Chan
*         Joshua Wei Han Ch'ng
*/

package src;

import ch.aplu.jgamegrid.*;
import java.util.*;

public class Orion extends Monster implements AggressiveFollowable {
    private final static String filename = "m_orion.gif";
    private final static int visitedLocationsLength = 30;
    private ArrayList<Gold> visitedGolds = new ArrayList<>();
    private ArrayList<Gold> eatenGolds = new ArrayList<>();
    private ArrayList<Gold> golds;
    private int allGoldsCount;
    private Gold target = null;
    public Orion(Game game, int seed, ArrayList<Gold> golds) {
        super(game, filename, seed, visitedLocationsLength);
        this.golds = golds;
        this.allGoldsCount = golds.size();
    }

    // Orion: Gold Surveillance to walk through every gold
    protected void walkApproach() {
        if (target == null || getLocation().equals(target.getLocation())) {
            target = getTarget();
            // DEBUG
//            if (!target.isVisible()) {
//                System.out.print("Eated: ");
//            }
//            System.out.println(target.getLocation());
        }
        // Use the Aggressive Follower walking approach to walk to the target gold location
        aggressiveFollow(target.getLocation());
    }

    // Choose a gold as the target
    private Gold getTarget() {
        Gold target;

        if (visitedGolds.size() == allGoldsCount) {
            visitedGolds.clear();
            // DEBUG
//            System.out.println("Clear");
        }

        // Move eated golds' locations to eatedGoldsLocation
        Iterator<Gold> iterator = golds.iterator();
        while (iterator.hasNext()) {
            Gold gold = iterator.next();
            if (!gold.isVisible()) {
                eatenGolds.add(gold);
                iterator.remove();
            }
        }

        // Favour existing golds' locations
        if ((target = selectGold(golds)) == null) {
            // All existing golds are eaten or visited before, select eaten golds
            target = selectGold(eatenGolds);
        }

        return target;
    }

    // Randomly select a non-visited gold from the selections
    private Gold selectGold(ArrayList<Gold> goldSelections) {
        Gold target = null;
        ArrayList<Gold> selections = new ArrayList<>(goldSelections);
        selections.removeAll(visitedGolds);  // Don't select visited golds' locations
        if (selections.size() > 0) {
            target = selections.get(getRandomiser().nextInt(selections.size()));
            visitedGolds.add(target);
        }
        return target;
    }
}
