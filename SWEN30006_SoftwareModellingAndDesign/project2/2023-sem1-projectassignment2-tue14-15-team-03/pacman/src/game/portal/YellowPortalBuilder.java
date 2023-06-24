/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.portal;

public class YellowPortalBuilder extends PortalBuilder{
    private static YellowPortalBuilder instance;
    private final static String filename = "sprites/portalYellowTile.png";
    private final static PortalType type = PortalType.YELLOW;

    private YellowPortalBuilder() {}
    public static YellowPortalBuilder getInstance() {
        if (instance == null) {
            instance = new YellowPortalBuilder();
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
