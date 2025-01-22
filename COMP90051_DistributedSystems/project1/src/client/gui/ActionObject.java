/* ActionObject.java
   Author: Thomas Choi 1202247 */

package client.gui;

import shared.OperationCode;

public class ActionObject {
    private String name;
    private OperationCode value;

    public ActionObject(String name, OperationCode value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public OperationCode getValue() {
        return value;
    }
}