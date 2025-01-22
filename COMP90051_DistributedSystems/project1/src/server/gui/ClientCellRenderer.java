/* ClientCellRenderer.java
   Author: Thomas Choi 1202247 */

package server.gui;

import server.ClientProfile;
import shared.Util;

import javax.swing.*;
import java.awt.*;

public class ClientCellRenderer extends JLabel implements ListCellRenderer {
    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

    public ClientCellRenderer() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        ClientProfile client = (ClientProfile) value;
        setText(String.format("%s: %s", Util.getIdStr(client.getId()), client.getAddrPort()));

        if (isSelected) {
            setBackground(HIGHLIGHT_COLOR);
            setForeground(Color.white);
        } else {
            setBackground(Color.white);
            setForeground(Color.black);
        }
        return this;
    }
}
