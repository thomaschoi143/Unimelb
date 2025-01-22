/* Server.java
   Author: Thomas Choi 1202247 */

package server;

import client.ClientRemoteObj;
import server.gui.ServerGUI;
import shared.host.Host;
import shared.host.HostProfile;
import shared.host.RemoteObj;
import shared.Util;
import shared.whiteboard.drawElements.DrawElement;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server extends Host implements ServerRemoteObj {
    private int nextClientId = 1;
    private FileManager fileManager;
    public static final int ID = 0;

    public Server(String port, String username) {
        super(port, username);
        setId(ID);
        this.fileManager = new FileManager(this);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar CreateWhiteBoard.jar <serverPort> <username>");
            System.exit(0);
        }

        Server server = new Server(args[0], args[1]);

        try {
            ServerRemoteObj stub = (ServerRemoteObj) UnicastRemoteObject.exportObject(server, 0);
            server.getRegistry().bind("RO", stub);

            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        ServerGUI gui = new ServerGUI(server);
                        gui.getFrame().setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void kick(int clientId) {
        ClientRemoteObj kickingClientStub = ((ClientRemoteObj)getPeers().get(clientId).getRemoteObj());
        for (Map.Entry<Integer, HostProfile> entry : getPeers().entrySet()) {
            if (entry.getKey() != clientId) {
                try {
                    entry.getValue().getRemoteObj().unregister(clientId);
                } catch (RemoteException e) {
                    getGui().getStatusInput().setText(String.format("Error updating client %s when kicking client", Util.getIdStr(entry.getKey())));
                    System.out.printf("Error updating client %s when kicking client: %s\n", entry.getKey(),  e.getMessage());
                    peerNotReachable(entry.getValue().getId());
                }
            }
        }
        removePeer(clientId);
        try {
            kickingClientStub.kick();
        } catch (RemoteException e) {
            getGui().getStatusInput().setText("Error when kicking client " + Util.getIdStr(clientId));
            System.out.printf("Error when kicking client %s: %s\n", clientId, e.getMessage());
        }
    }

    public void newWhiteBoard(ArrayList<DrawElement> drawElements) {
        getWhiteBoard().setDrawElements(drawElements);
        for (Map.Entry<Integer, HostProfile> entry : getPeers().entrySet()) {
            try {
                ((ClientRemoteObj)entry.getValue().getRemoteObj()).newWhiteBoard(drawElements);
            } catch (RemoteException e) {
                getGui().getStatusInput().setText("Error when clearing white board for client " + Util.getIdStr(entry.getKey()));
                System.out.println("Error when clearing white board for client: " + entry.getKey());
                peerNotReachable(entry.getValue().getId());
            }
        }
    }

    public Integer register(Integer id, RemoteObj stub, String username) throws RemoteException {
        boolean isApprove = getGui().askDialog(String.format("Do you approve new client %s?", username), "New Client");
        int clientId = nextClientId++;
        if (isApprove) {
            addPeer(new HostProfile(clientId, username, stub));
            return clientId;
        }
        return null;
    }

    public HashMap<Integer, HostProfile> retrievePeers() throws RemoteException {
        return getPeers();
    }

    public ArrayList<DrawElement> retrieveDrawElements() throws  RemoteException {
        return getWhiteBoard().getDrawElements();
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
