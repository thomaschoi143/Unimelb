/* ChatPanel.java
   Author: Thomas Choi 1202247 */

package shared.chatroom;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import shared.host.Host;
import shared.gui.JListCellRenderer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JPanel {
    private DefaultListModel<MessageProfile> messagesModel = new DefaultListModel<>();
    private JPanel panel;
    public ChatPanel(Host host) {

        panel = new JPanel(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("max(110dlu;default):grow"),},
                new RowSpec[] {
                        RowSpec.decode("max(10dlu;default)"),
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("max(190dlu;default)"),
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("max(25dlu;default)"),}));

        JLabel chatroomLabel = new JLabel("Chatroom (hit Enter to send)");
        panel.add(chatroomLabel, "1, 1, left, fill");

        JList messagesList = new JList<>(messagesModel);
        messagesList.setCellRenderer(new JListCellRenderer());
        JScrollPane messagesListScroll = new JScrollPane (messagesList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(messagesListScroll, "1, 3, fill, fill");

        JTextField chatInput = new JTextField();
        panel.add(chatInput, "1, 5, fill, default");

        chatInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                host.broadcastMessage(chatInput.getText());
                chatInput.setText("");
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    public DefaultListModel<MessageProfile> getMessagesModel() {
        return messagesModel;
    }
}
