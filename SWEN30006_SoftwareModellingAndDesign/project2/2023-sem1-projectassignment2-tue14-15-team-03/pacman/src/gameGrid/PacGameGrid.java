/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.gameGrid;

public interface PacGameGrid {
    /**
     * Returns the width of the map.
     * @return int The width of the total map.
     */
    int getWidth();

    /**
     * Returns the height of the total map.
     * @return int The height of the map.
     */
    int getHeight();

    /**
     * The the value of a tile.
     * @param x The X-coordinate.
     * @param y The Y-coordinate.
     * @return char The character on the tile.
     */
    char getTile(int x, int y);

    default boolean isInBound(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return false;
        }
        return true;
    }
}
