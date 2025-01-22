/* ObjectListCellRenderer.java
   Author: Thomas Choi 1202247 */

package client.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ObjectListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(
                                   JList list,
                                   Object value,
                                   int index,
                                   boolean isSelected,
                                   boolean cellHasFocus) {
        if (value instanceof ActionObject) {
            value = ((ActionObject)value).getName();
        }
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        return this;
    }
}