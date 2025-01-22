/* RemoteObj.java
   Author: Thomas Choi 1202247 */

package shared.host;

import shared.chatroom.MessageProfile;
import shared.whiteboard.drawElements.DrawElement;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteObj extends Remote {
    Integer register(Integer id, RemoteObj stub, String username) throws RemoteException;
    void unregister(Integer id) throws RemoteException;
    void addShape(DrawElement s) throws RemoteException;
    void sendMessage(MessageProfile message) throws RemoteException;
    String retrieveUsername() throws  RemoteException;
}
