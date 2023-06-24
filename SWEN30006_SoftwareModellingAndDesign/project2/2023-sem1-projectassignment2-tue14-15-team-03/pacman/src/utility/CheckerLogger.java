/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CheckerLogger {
    private static CheckerLogger instance;
    private FileWriter fileWriter = null;

    private CheckerLogger() {
        String logFilePath = "ErrorLog.txt";
        try {
            fileWriter = new FileWriter(new File(logFilePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static CheckerLogger getInstance() {
        if (instance == null) {
            instance = new CheckerLogger();
        }
        return instance;
    }

    public void writeString(File mapFile, String str) {
        String outputStr;
        if (mapFile.isDirectory()) {
            outputStr = String.format("Game %s – %s", mapFile.getName(), str);
        } else {
            outputStr = String.format("Level %s – %s", mapFile.getName(), str);
        }

        try {
            fileWriter.write(outputStr);
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
