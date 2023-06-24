/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.portal;

public class WhitePortalBuilder extends PortalBuilder{
    private static WhitePortalBuilder instance;
    private final static String filename = "sprites/portalWhiteTile.png";
    private final static PortalType type = PortalType.WHITE;
    private WhitePortalBuilder() {}
    public static WhitePortalBuilder getInstance() {
        if (instance == null) {
            instance = new WhitePortalBuilder();
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
