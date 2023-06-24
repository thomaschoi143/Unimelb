/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src;

import ch.aplu.jgamegrid.*;

public class PacManGameGrid {
    public final static int nbHorzCells = 20;
    public final static int nbVertCells = 11;
    private final static String defaultMaze =
            "xxxxxxxxxxxxxxxxxxxx" + // 0
            "x....x....g...x....x" + // 1
            "xgxx.x.xxxxxx.x.xx.x" + // 2
            "x.x.......i.g....x.x" + // 3
            "x.x.xx.xx  xx.xx.x.x" + // 4
            "x......x    x......x" + // 5
            "x.x.xx.xxxxxx.xx.x.x" + // 6
            "x.x......gi......x.x" + // 7
            "xixx.x.xxxxxx.x.xx.x" + // 8
            "x...gx....g...x....x" + // 9
            "xxxxxxxxxxxxxxxxxxxx";  // 10
    private GridElementType[][] defaultMazeArray;

    public PacManGameGrid() {
        defaultMazeArray = new GridElementType[nbVertCells][nbHorzCells];

        // Copy structure into integer array
        for (int i = 0; i < nbVertCells; i++) {
            for (int k = 0; k < nbHorzCells; k++) {
                GridElementType value = toElementType(defaultMaze.charAt(nbHorzCells * i + k));
                defaultMazeArray[i][k] = value;
            }
        }
    }

    public GridElementType getDefaultCell(Location location)
    {
      return defaultMazeArray[location.y][location.x];
    }

    // Convert char in defaultMaze to GridElementType Enum
    private GridElementType toElementType(char c) {
        switch(c) {
            case 'x':
                return GridElementType.BLOCK;
            case '.':
                return GridElementType.PILL;
            case ' ':
                return GridElementType.SPACE;
            case 'g':
                return GridElementType.GOLD;
            case 'i':
                return GridElementType.ICE;
            default:
                return GridElementType.UNDEFINED;
        }
    }
}
