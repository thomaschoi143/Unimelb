/* DictionaryServer.java
   Author: Thomas Choi 1202247 */

package server;

import server.gui.ServerGUI;
import shared.Util;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DictionaryServer extends Thread {
    private ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
    private int port;
    private WordDictionary dict = null;
    private int clientId = 1;
    private ServerSocket listeningSocket;
    private Logger logger;
    private ServerGUI gui;
    private ArrayList<ClientProfile> clients = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DictionaryServer.jar <port> <dictionary-file>");
            System.exit(0);
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ServerGUI window = new ServerGUI(args[0], args[1]);
                    window.getFrame().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public DictionaryServer(ServerGUI gui) {
        super();
        logger = new Logger(gui);
        this.gui = gui;
    }

    public StatusCode startServer(String portStr, String filepath) {
        try {
            dict = new WordDictionary(filepath, this);
        } catch (FileNotFoundException e) {
            logger.addMessage("Could not find file: " + filepath);
            return StatusCode.NO_DICT;
        } catch (IOException e) {
            logger.addMessage("Could not open file: %s" + filepath);
            return StatusCode.NO_DICT;
        }

        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            logger.addMessage("Port number should be an int");
            return StatusCode.STOPPED;
        }

        // Save the dictionary every 1 minute
        Runnable saveRunnable = new Runnable() {
            public void run() {
                synchronized (dict) {
                    dict.saveDictionary(gui.getFilepathInput().getText());
                }
            }
        };

        exec.scheduleAtFixedRate(saveRunnable , 1, 1, TimeUnit.MINUTES);

        try {
            listeningSocket = new ServerSocket(port);
            logger.addMessage("Server listening on port " + port);
        } catch (IllegalArgumentException e) {
            logger.addMessage("Port number must be between 0 and 65535");
            return StatusCode.STOPPED;
        } catch (IOException e) {
            logger.addMessage("An error when opening socket");
            return StatusCode.STOPPED;
        }

        return StatusCode.RUNNING;
    }

    public void run() {
        Socket client;
        try {
            while (true) {
                client = listeningSocket.accept();
                ClientProfile clientProfile = new ClientProfile(clientId, client);
                logger.addMessage(String.format("Client %s from %s accepted", Util.getIdStr(clientProfile.getId()),
                        clientProfile.getAddrPort()));
                clients.add(clientProfile);
                gui.getClientsModel().addElement(clientProfile);
                gui.getClientsNum().setText(String.valueOf(clients.size()));
                Thread t = new ClientHandler(clientProfile, this);
                t.start();
                clientId++;
            }

        } catch (SocketException e) {
            String message = "Server cannot accept client socket";
            System.out.println(message);
            logger.addMessage(message);
        } catch (IOException e) {
            String message = "Server IO: " + e.getMessage();
            System.out.println(message);
            logger.addMessage(message);
        }
    }

    public void shutdown() {
        String message = "";
        if (listeningSocket != null) {
            try {
                listeningSocket.close();
                message = "Closed listening socket";
            } catch (IOException e) {
                message = "Cannot close server listening socket";
            }
        }
        System.out.println(message);
        logger.addMessage(message);
        System.exit(0);
    }

    public void kickClient(ClientProfile client) {
        try {
            client.getSocket().close();
        } catch (IOException error) {
            logger.addMessage(String.format("Client %s: Error when closing connection", Util.getIdStr(client.getId())));
        }
    }

    public void clientExit(ClientProfile client) {
        clients.remove(client);
        gui.getClientsModel().removeElement(client);
        gui.getClientsNum().setText(String.valueOf(clients.size()));
    }

    public WordDictionary getDict() {
        return dict;
    }

    public Logger getLogger() {
        return logger;
    }

    public int getClientsNum() {
        return clients.size();
    }
}
