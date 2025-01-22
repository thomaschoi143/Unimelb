/* Client.java
   Author: Thomas Choi 1202247 */

package client;

import server.Server;
import server.ServerRemoteObj;
import shared.Util;
import shared.host.Host;
import shared.host.HostProfile;
import shared.host.RemoteObj;
import shared.gui.BasicGUI;
import shared.whiteboard.drawElements.DrawElement;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;

public class Client extends Host implements ClientRemoteObj {

    public Client(String registryPort, String username) {
        super(registryPort, username);
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java -jar JoinWhiteBoard.jar <managerIPAddress> <managerPort> <registryPort> <username>");
            System.exit(0);
        }

        Client client = new Client(args[2], args[3]);

        try {
            RemoteObj stub = (RemoteObj) UnicastRemoteObject.exportObject(client, 0);
            client.getRegistry().bind("RO", stub);

            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        BasicGUI window = new BasicGUI("Client GUI", client);
                        window.getFrame().setVisible(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.connectToManager(args[0], args[1]);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void connectToManager(String serverAddr, String serverPort) {
        while(getGui() == null || getGui().getStatusInput() == null) {
            System.out.println("waiting for manager...");
        }
        JTextField statusInput = getGui().getStatusInput();
        statusInput.setText("Connecting to manager...");
        try {
            int serverPortNum = Integer.parseInt(serverPort);
            Registry serverRegistry = LocateRegistry.getRegistry(serverAddr, serverPortNum);
            ServerRemoteObj server = (ServerRemoteObj) serverRegistry.lookup("RO");

            RemoteObj myStub = (RemoteObj)getRegistry().lookup("RO");
            Integer myId = server.register(getId(), myStub, getUsername());

            if (myId != null) {
                setId(myId);

                addPeer(new HostProfile(Server.ID, server.retrieveUsername(), server));
                for(Map.Entry<Integer, HostProfile> entry : server.retrievePeers().entrySet()) {
                    if (!entry.getKey().equals(myId)) {
                        addPeer(entry.getValue());
                        try {
                            entry.getValue().getRemoteObj().register(myId, myStub, getUsername());
                        } catch(RemoteException e) {
                            statusInput.setText("Error when communicating to peer " + Util.getIdStr(entry.getKey()));
                            System.out.printf("Error when communicating to peer %s: %s\n", Util.getIdStr(entry.getKey()), e.getMessage());
                            peerNotReachable(entry.getKey());
                        }
                    }
                }

                getWhiteBoard().setDrawElements(server.retrieveDrawElements());
                getGui().putWhiteBoard(getWhiteBoard());

                statusInput.setText("Successfully connected to manager");

            } else {
                statusInput.setText("Manager rejected you");
            }

        } catch (NumberFormatException e) {
            statusInput.setText("Port Number Must be an Integer");
        } catch(Exception e) {
            System.out.println(e.getMessage());
            statusInput.setText("Error when connecting to manager");
        }
    }

    public Integer register(Integer id, RemoteObj stub, String username) throws RemoteException {
        addPeer(new HostProfile(id, username, stub));
        return getId();
    }

    public void kick() throws RemoteException {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                getGui().notificationDialog("The application will be closed", "Manager Kicked You Out");
                System.exit(0);
            }
        });
    }

    public void newWhiteBoard(ArrayList<DrawElement> drawElements) throws RemoteException {
        getGui().getStatusInput().setText("Manager created a new white board");
        getWhiteBoard().setDrawElements(drawElements);
    }
}
