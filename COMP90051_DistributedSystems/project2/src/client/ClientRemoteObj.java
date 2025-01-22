/* ClientRemoteObj.java
   Author: Thomas Choi 1202247 */

package client;

import shared.host.RemoteObj;
import shared.whiteboard.drawElements.DrawElement;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClientRemoteObj extends RemoteObj {
    void kick() throws RemoteException;
    void newWhiteBoard(ArrayList<DrawElement> drawElements) throws RemoteException;
}
