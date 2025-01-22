/* DictionaryClient.java
   Author: Thomas Choi 1202247 */

package client;

import client.gui.*;
import shared.OperationCode;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class DictionaryClient {
    private Socket server;
    private ObjectOutputStream out;
    private DataInputStream in;
    public final static String CONNECTED = "Connected!";
    public final static String NOT_CONNECTED = "Connection Error!";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DictionaryClient.jar <server-address> <server-port>");
            System.exit(0);
        }

        DictionaryClient client = new DictionaryClient();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClientGUI window = new ClientGUI(client, args[0], args[1]);
                    window.getFrame().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String connectToServer(String serverAddress, String portStr) {
        Integer serverPort = null;
        try {
            serverPort = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return "Port Number Must be an Integer";
        }
        try {
            server = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(server.getOutputStream());
            in = new DataInputStream(server.getInputStream());
            return CONNECTED;
        } catch (UnknownHostException e) {
            return "Unknown Host";
        } catch (IOException e) {
            return NOT_CONNECTED;
        } catch (IllegalArgumentException e) {
            return "Port Number Must be Between 0 and 65535";
        }
    }

    public String sendRequest(OperationCode operation, String word, String meanings) {
        ClientPacket request;

        try {

            if (operation == OperationCode.SEARCH || operation == OperationCode.REMOVE) {
                request = new ClientPacket(operation, word);
                out.writeObject(request);

                return in.readUTF();
            } else if (operation == OperationCode.ADD || operation == OperationCode.UPDATE) {
                String[] meaningsArr = meanings.split(";");
                for (int i = 0; i < meaningsArr.length; i++) {
                    meaningsArr[i] = meaningsArr[i].trim();
                }

                LinkedHashSet<String> meaningsSet
                        = new LinkedHashSet<>(Arrays.asList(meaningsArr));
                request = new ClientPacket(operation, word, meaningsSet);
                out.writeObject(request);

                return in.readUTF();
            } else {
                return "Error: Unknown action";
            }
        } catch (IOException e) {
            return NOT_CONNECTED;
        }
    }

    public String closeConnection() {
        if (server != null) {
            try {
                server.close();
                return "Closed connection to server";
            } catch (IOException e) {
                return "Error: An error when closing connection to server";
            }
        }
        return "Closed connection to server";
    }
}
