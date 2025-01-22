/* ClientProfile.java
   Author: Thomas Choi 1202247 */

package server;

import java.net.Socket;

public class ClientProfile {
    private int id;
    private Socket socket;

    public ClientProfile(int id, Socket socket) {
        this.id = id;
        this.socket = socket;
    }

    public int getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getAddrPort() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }
}
