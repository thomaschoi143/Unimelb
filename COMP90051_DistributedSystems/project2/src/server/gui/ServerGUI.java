/* ServerGUI.java
   Author: Thomas Choi 1202247 */

package server.gui;

import server.Server;
import shared.host.HostProfile;
import shared.Util;
import shared.gui.BasicGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServerGUI extends BasicGUI {
    private static final String TITLE = "Manager GUI";
    public ServerGUI(Server server) {
        super(TITLE, server);
        JFrame frame = super.getFrame();

        JLabel fileActionSelectLabel = new JLabel("Whiteboard Action");
        frame.getContentPane().add(fileActionSelectLabel, "6, 6, right, default");

        JPanel fileActionPanel = new JPanel();

        JButton fileNewActionBtn = new JButton("New");
        fileActionPanel.add(fileNewActionBtn);

        JButton fileOpenActionBtn = new JButton("Open");
        fileActionPanel.add(fileOpenActionBtn);

        JButton fileSaveBtn = new JButton("Save");
        fileActionPanel.add(fileSaveBtn);

        JButton fileSaveAsBtn = new JButton("Save As");
        fileActionPanel.add(fileSaveAsBtn);

        frame.getContentPane().add(fileActionPanel, "8, 6, 4, 1, fill, default");

        putWhiteBoard(server.getWhiteBoard());

        // listeners
        JList peersList = getPeersList();
        peersList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int index = peersList.locationToIndex(evt.getPoint());
                if (index >= 0) {
                    HostProfile client = getPeersModel().getElementAt(index);
                    if (JOptionPane.showConfirmDialog(frame,
                            String.format("Are you sure you want to kick out client %s(%s)", client.getUsername(), Util.getIdStr(client.getId())), "Kick Client?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        server.kick(client.getId());
                    }
                }
            }
        });

        fileNewActionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to create a new white board? The current one will be lost.", "New White Board?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    server.getFileManager().newWhiteBoard();
                }
            }
        });

        fileOpenActionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to open a white board from a file? The current one will be lost.", "Open White Board?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    server.getFileManager().open();
                }
            }
        });

        fileSaveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.getFileManager().save(server.getWhiteBoard().getDrawElements());
            }
        });

        fileSaveAsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.getFileManager().saveAs(server.getWhiteBoard().getDrawElements());
            }
        });
    }
}
