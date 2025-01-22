/* Host.java
   Author: Thomas Choi 1202247 */

package shared.host;

import server.Server;
import shared.Util;
import shared.chatroom.ChatPanel;
import shared.chatroom.MessageProfile;
import shared.gui.BasicGUI;
import shared.whiteboard.WhiteBoard;
import shared.whiteboard.drawElements.DrawElement;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public abstract class Host implements RemoteObj {
    private WhiteBoard whiteBoard;
    private BasicGUI gui;
    private final String username;
    private Registry registry;
    private int id;
    private ChatPanel chatPanel;
    private HashMap<Integer, HostProfile> peers = new HashMap<>();
    public Host(String registryPort, String username)  {
        this.username = username;
        whiteBoard = new WhiteBoard(this);
        Host host = this;
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                chatPanel = new ChatPanel(host);
            }
        });

        try {
            int registryPortNum = Integer.parseInt(registryPort);
            this.registry = LocateRegistry.createRegistry(registryPortNum);
        } catch (NumberFormatException e) {
            getGui().getStatusInput().setText("Port Number Must be an Integer");
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void broadcastShape(DrawElement s) {
        for (Map.Entry<Integer, HostProfile> entry : peers.entrySet()) {
            try {
                entry.getValue().getRemoteObj().addShape(s);
            } catch(RemoteException e) {
                gui.getStatusInput().setText("Error when communicating to peer " + Util.getIdStr(entry.getKey()));
                System.out.printf("Error when communicating to peer %s: %s\n", Util.getIdStr(entry.getKey()), e.getMessage());
                peerNotReachable(entry.getValue().getId());
            }
        }
    }

    public void broadcastQuit() {
        for (Map.Entry<Integer, HostProfile> entry : peers.entrySet()) {
            try {
                entry.getValue().getRemoteObj().unregister(getId());
            } catch(RemoteException e) {
                gui.getStatusInput().setText("Error when communicating to peer " + Util.getIdStr(entry.getKey()));
                System.out.printf("Error when communicating to peer %s: %s\n", Util.getIdStr(entry.getKey()), e.getMessage());
            }
        }
        System.exit(0);
    }

    public void broadcastMessage(String text) {
        MessageProfile message = new MessageProfile(username, id, text);
        chatPanel.getMessagesModel().addElement(message);
        for (Map.Entry<Integer, HostProfile> entry : peers.entrySet()) {
            try {
                entry.getValue().getRemoteObj().sendMessage(message);
            } catch(RemoteException e) {
                gui.getStatusInput().setText("Error when communicating to peer " + Util.getIdStr(entry.getKey()));
                System.out.printf("Error when communicating to peer %s: %s\n", Util.getIdStr(entry.getKey()), e.getMessage());
                peerNotReachable(entry.getValue().getId());
            }
        }
    }

    public void unregister(Integer id) throws RemoteException {
        if (id == Server.ID) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    gui.notificationDialog("The application will be closed", "Manager has left");
                    System.exit(0);
                }
            });
        } else {
            removePeer(id);
        }
    }

    public String retrieveUsername() throws RemoteException {
        return getUsername();
    }

    public void addShape(DrawElement s) throws RemoteException {
        whiteBoard.addShape(s);
    }

    public void sendMessage(MessageProfile message) throws RemoteException {
        chatPanel.getMessagesModel().addElement(message);
    }

    public HashMap<Integer, HostProfile> getPeers() {
        return peers;
    }

    public void addPeer(HostProfile peer) {
        peers.put(peer.getId(), peer);
        gui.getStatusInput().setText(String.format("Peer %s(%s) joined", peer.getUsername(), Util.getIdStr(peer.getId())));
        gui.getPeersModel().addElement(peer);
        gui.getFrame().revalidate();
    }

    public void removePeer(int id) {
        if (peers.containsKey(id)) {
            HostProfile peer = peers.get(id);
            peers.remove(id);
            gui.getStatusInput().setText(String.format("Peer %s(%s) left", peer.getUsername(), Util.getIdStr(id)));
            gui.getPeersModel().removeElement(peer);
            gui.getFrame().revalidate();
        }
    }

    public void peerNotReachable(int id) {
        if (id == Server.ID) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    gui.notificationDialog("The application will be closed", "Manager is not reachable");
                    System.exit(0);
                }
            });
        }
        if (peers.containsKey(id)) {
            HostProfile peer = peers.get(id);
            peers.remove(id);
            gui.getStatusInput().setText(String.format("Peer %s(%s) is not reachable", peer.getUsername(), Util.getIdStr(id)));
            gui.getPeersModel().removeElement(peer);
            gui.getFrame().revalidate();
        }
    }

    public void setGui(BasicGUI gui) {
        this.gui = gui;
    }
    public WhiteBoard getWhiteBoard() {
        return whiteBoard;
    }

    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    public BasicGUI getGui() {
        return gui;
    }

    public String getUsername() {
        return username;
    }

    public Registry getRegistry() {
        return registry;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
