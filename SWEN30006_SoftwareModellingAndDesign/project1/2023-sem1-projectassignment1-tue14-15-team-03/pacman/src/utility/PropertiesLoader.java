package src.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesLoader {
    public static Properties loadPropertiesFile(String propertiesFile) {
        try (InputStream input = new FileInputStream(propertiesFile)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);
            if( prop.getProperty("PacMan.move").equals("")){
                prop.remove("PacMan.move");
            }

            if( prop.getProperty("Pills.location").equals("")){
                prop.remove("Pills.location");
            }

            if( prop.getProperty("Gold.location").equals("")){
                prop.remove("Gold.location");
            }
            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
