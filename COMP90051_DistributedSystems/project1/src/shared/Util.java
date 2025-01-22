/* Util.java
   Author: Thomas Choi 1202247 */

package shared;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getIdStr(int clientId) {
        return String.format("%04d", clientId);
    }
}
