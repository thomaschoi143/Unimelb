/* Logger.java
   Author: Thomas Choi 1202247 */

package server;

import server.gui.ServerGUI;
import shared.Util;

public class Logger {
    private String messages = "";
    private ServerGUI gui;

    public Logger(ServerGUI gui) {
        this.gui = gui;
        gui.getLogArea().setText(messages);
    }

    public void addMessage(String m) {
        messages += String.format("%s: %s\n", Util.getCurrentDate(), m);
        gui.getLogArea().setText(messages);
    }
}
