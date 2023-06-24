/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.mapeditor.checker.gameChecking;

import src.utility.CheckerLogger;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OneMapPerNumberRule implements GameCheckingStrategy{
    public boolean check(File folder) {

        File[] files = folder.listFiles();
        HashMap<Integer, ArrayList<String>> fileNames = new HashMap<>();
        Integer currentLevel;
        boolean noError = true;

        // Map filename has to start with a number
        Pattern p = Pattern.compile("^\\d+");
        for (File file : files) {
            Matcher m = p.matcher(file.getName());
            if (m.find() && file.getName().endsWith(".xml")) {
                currentLevel = Integer.parseInt(m.group());

                if (!fileNames.containsKey(currentLevel)) {
                    fileNames.put(currentLevel, new ArrayList<>());
                }
                fileNames.get(currentLevel).add(file.getName());

            }


        }

        String str = "multiple maps at same level: ";
        for (Integer level : fileNames.keySet()) {
            int numberOfFiles = fileNames.get(level).size();
            if (numberOfFiles > 1) {
                for (String fileName: fileNames.get(level)) {
                    str += (fileName + "; ");
                }

                noError = false;
            }
        }

        if (!noError) {
            str = str.substring(0, str.length() - 2);
            CheckerLogger.getInstance().writeString(folder, str);
        }

        return noError;
    }
}
