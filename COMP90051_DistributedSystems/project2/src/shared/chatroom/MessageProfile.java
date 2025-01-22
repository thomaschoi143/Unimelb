/* MessageProfile.java
   Author: Thomas Choi 1202247 */

package shared.chatroom;

import java.io.Serializable;

public class MessageProfile implements Serializable {
    private String username;
    private int id;
    private String text;

    private static final long serialVersionUID = 12345673971L;
    public MessageProfile(String username, int id, String text) {
        this.username = username;
        this.id = id;
        this.text = text;
    }


    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
