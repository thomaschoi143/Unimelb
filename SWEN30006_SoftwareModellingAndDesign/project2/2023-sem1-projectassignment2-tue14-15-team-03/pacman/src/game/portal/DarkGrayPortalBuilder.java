/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.portal;

public class DarkGrayPortalBuilder extends PortalBuilder{
    private static DarkGrayPortalBuilder instance;
    private final static String filename = "sprites/portalDarkGrayTile.png";
    private final static PortalType type = PortalType.DARKGRAY;
    private DarkGrayPortalBuilder() {}
    public static DarkGrayPortalBuilder getInstance() {
        if (instance == null) {
            instance = new DarkGrayPortalBuilder();
        }
        return instance;
    }
    protected void setType() {
        portal.setType(type);
    }
    protected void setPortalActor() {
        portal = new Portal(filename);
    }
}
