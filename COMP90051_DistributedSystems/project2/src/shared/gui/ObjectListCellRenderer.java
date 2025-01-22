/* ObjectListCellRenderer.java
   Author: Thomas Choi 1202247 */

package shared.gui;

import javax.swing.*;
import java.awt.*;

public class ObjectListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        if (value instanceof ActionOption) {
            value = ((ActionOption)value).getName();
        } else if (value instanceof ColorOption) {
            value = ((ColorOption)value).getName();
        }
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        return this;
    }
}
