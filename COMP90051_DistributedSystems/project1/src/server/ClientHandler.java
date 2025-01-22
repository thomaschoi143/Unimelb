/* ClientHandler.java
   Author: Thomas Choi 1202247 */

package server;

import client.ClientPacket;
import shared.OperationCode;
import shared.Util;

import java.io.*;
import java.net.SocketException;

import java.util.LinkedHashSet;


public class ClientHandler extends Thread{
    private final ClientProfile client;
    private final DictionaryServer server;

    public ClientHandler(ClientProfile client, DictionaryServer server) {
        super();
        this.client = client;
        this.server = server;
    }

    public void run () {
        try {
            ObjectInputStream in = new ObjectInputStream(client.getSocket().getInputStream());
            DataOutputStream out = new DataOutputStream(client.getSocket().getOutputStream());

            Object obj = null;
            String response = "";

            while (true) {
                try {
                    obj = in.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    response = "Error: An error when receiving request in server";
                }  catch (EOFException e) {
                    break;
                }
                if (obj instanceof ClientPacket request) {
                    OperationCode operation = request.getCode();
                    String word = request.getWord();
                    LinkedHashSet<String> meanings = request.getMeanings();

                    server.getLogger().addMessage(String.format("Request from client %s:\noperation: %s, word: '%s', meanings: '%s'",
                            Util.getIdStr(client.getId()), operation, word, meanings));

                    if (word.length() == 0) {
                        response =  "Error: Word is empty";
                    } else {
                        WordDictionary dict = server.getDict();
                        if (operation == OperationCode.SEARCH) {
                            synchronized (dict) {
                                response = dict.search(word);
                            }
                            
                        } else if (operation == OperationCode.REMOVE){
                            synchronized (dict) {
                                response = dict.removeWord(word);
                            }
                            
                        } else if (operation == OperationCode.ADD) {
                            synchronized (dict) {
                                response = dict.addWord(word, meanings);
                            }

                        } else if (operation == OperationCode.UPDATE) {
                            synchronized (dict) {
                                response = dict.updateMeaning(word, meanings);
                            }

                        } else {
                                response = "Error: Unknown operation";
                        }
                    }
                }
                server.getLogger().addMessage(String.format("Response to client %s:\n%s", Util.getIdStr(client.getId()), response));
                out.writeUTF(response);
                out.flush();
            }
            server.getLogger().addMessage(String.format("Client %s: Received EOF, connection closed", Util.getIdStr(client.getId())));
            client.getSocket().close();
            server.clientExit(client);
        } catch (SocketException e) {
            server.getLogger().addMessage(String.format("Client %s: Actively kicked or socket corrupted", Util.getIdStr(client.getId())));
            server.clientExit(client);
        } catch (IOException e) {
            server.getLogger().addMessage(String.format("Client %s: Error when connecting to client", Util.getIdStr(client.getId())));
            server.clientExit(client);
        }
    }
}
