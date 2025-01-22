/* ActionOption.java
   Author: Thomas Choi 1202247 */

package shared.gui;

import shared.whiteboard.ActionCode;

public class ActionOption {
    private String name;
    private ActionCode value;

    public ActionOption(String name, ActionCode value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public ActionCode getValue() {
        return value;
    }
}