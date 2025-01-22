/* ServerRemoteObj.java
   Author: Thomas Choi 1202247 */

package server;

import shared.host.HostProfile;
import shared.host.RemoteObj;
import shared.whiteboard.drawElements.DrawElement;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ServerRemoteObj extends RemoteObj {
    HashMap<Integer, HostProfile> retrievePeers() throws RemoteException;
    ArrayList<DrawElement> retrieveDrawElements() throws RemoteException;
}
