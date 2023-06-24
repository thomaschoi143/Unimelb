/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.portal;

import ch.aplu.jgamegrid.Actor;

public abstract class PortalBuilder{

    protected Portal portal;
    protected abstract void setType();
    protected abstract void setPortalActor();

    public Portal buildPortal() {
        setPortalActor();
        setType();
        return portal;
    }

}
