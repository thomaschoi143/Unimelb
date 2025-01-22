/* ColorOption.java
   Author: Thomas Choi 1202247 */

package shared.gui;

import java.awt.*;

public class ColorOption {
    private String name;
    private Color value;

    public ColorOption(String name, Color value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Color getValue() {
        return value;
    }
}