/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.game.item;

public abstract class ItemBuilder {
    protected Item item;
    protected abstract void setItemActor();
    protected abstract void setColor();
    protected abstract void setEarningScore();
    protected abstract void setItemType();
    public Item buildItem() {
        setItemActor();
        setColor();
        setEarningScore();
        setItemType();
        return item;
    }
}
