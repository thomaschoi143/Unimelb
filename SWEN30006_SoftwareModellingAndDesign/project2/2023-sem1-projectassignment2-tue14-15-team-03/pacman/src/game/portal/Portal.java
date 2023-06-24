/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.portal;

import ch.aplu.jgamegrid.Actor;

public class Portal extends Actor{
    private Portal twinPortal;
    private boolean isActive = false;
    private PortalType type;

    public Portal(String filename) {
        super(filename);
    }

    public Portal getTwinPortal(){
        return twinPortal;
    }

    public boolean isActive() {
        return isActive;
    }

    public PortalType getType() {
        return type;
    }
    public void setTwinPortal(Portal twinPortal){
        this.twinPortal = twinPortal;
    }

    public void setActive(boolean isActive){
        this.isActive = isActive;
    }

    public void setType(PortalType type) {
        this.type = type;
    }
}
