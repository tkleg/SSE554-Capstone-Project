package org.troy.capstone;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static boolean graphBuildingEnabled;

    static{
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties properties = new Properties();
            if (input != null) {
                properties.load(input);
                graphBuildingEnabled = Boolean.parseBoolean(properties.getProperty("graphBuildingEnabled"));
            }
        } catch (Exception e) {
            graphBuildingEnabled = false;
        }
    }
}
