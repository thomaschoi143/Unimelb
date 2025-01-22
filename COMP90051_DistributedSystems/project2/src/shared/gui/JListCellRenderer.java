/* JListCellRenderer.java
   Author: Thomas Choi 1202247 */

package shared.gui;

import shared.host.HostProfile;
import shared.Util;
import shared.chatroom.MessageProfile;

import javax.swing.*;
import java.awt.*;

public class JListCellRenderer extends JLabel implements ListCellRenderer {
    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

    public JListCellRenderer() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof HostProfile) {
            HostProfile peer = (HostProfile) value;
            setText(String.format("%s(%s)", peer.getUsername(), Util.getIdStr(peer.getId())));
        } else if (value instanceof MessageProfile) {
            MessageProfile message = (MessageProfile) value;
            setText(String.format("%s(%s): %s", message.getUsername(), Util.getIdStr(message.getId()), message.getText()));
        }


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