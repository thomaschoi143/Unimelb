/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */

package src.game;

import ch.aplu.jgamegrid.*;
import java.awt.*;

public class BackgroundHandler {
    private final static Color wallColor = Color.gray;
    private final static Color spaceColor = Color.lightGray;
    private final static Color portalColor = Color.black;
    private final static int backgroundRadius = 5;
    private GGBackground bg;

    public BackgroundHandler(GGBackground bg) {
        this.bg = bg;
        this.bg.clear(wallColor);
    }

    public void drawSpace(Location location) {
        bg.fillCell(location, spaceColor);
    }
    public void drawItem(Color color, Point point) {
        bg.setPaintColor(color);
        bg.fillCircle(point, backgroundRadius);
    }
    public void drawPortal(Location location) {
        bg.fillCell(location, portalColor);
    }

    // Check if the location has an item or not
    public boolean isItemLocation(Location location) {
        Color locationColor = bg.getColor(location);
        return (!locationColor.equals(spaceColor) && !locationColor.equals(wallColor) && !locationColor.equals(portalColor));
    }

    public boolean isWall(Location location) {
        Color locationColor = bg.getColor(location);
        return (locationColor.equals(wallColor));
    }

    public boolean isPortal(Location location) {
        return (bg.getColor(location).equals(portalColor));
    }

}
