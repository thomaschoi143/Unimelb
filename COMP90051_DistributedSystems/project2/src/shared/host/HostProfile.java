/* HostProfile.java
   Author: Thomas Choi 1202247 */

package shared.host;

import java.io.Serializable;

public class HostProfile implements Serializable {
    private final String username;
    private final int id;
    private final RemoteObj remoteObj;

    private static final long serialVersionUID = 12345671231L;
    public HostProfile(int id, String username, RemoteObj remoteObj) {
        this.id = id;
        this.username = username;
        this.remoteObj = remoteObj;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public RemoteObj getRemoteObj() {
        return remoteObj;
    }
}
