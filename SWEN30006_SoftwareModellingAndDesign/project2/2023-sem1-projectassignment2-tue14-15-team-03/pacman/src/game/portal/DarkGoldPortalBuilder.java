/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.portal;

public class DarkGoldPortalBuilder extends PortalBuilder {
    private static DarkGoldPortalBuilder instance;
    private final static String filename = "sprites/portalDarkGoldTile.png";
    private final static PortalType type = PortalType.DARKGOLD;
    private DarkGoldPortalBuilder() {}
    public static DarkGoldPortalBuilder getInstance() {
        if (instance == null) {
            instance = new DarkGoldPortalBuilder();
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
