package org.troy.capstone;

import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for the application.
 * Loads properties from the {@code app.properties} file in the resources folder and provides access via static fields.
 * All work done in the static initializes block to ensure properties are loaded before any access.
 */
public class Config {

    /** Flag to enable or disable graph building. Defaults to false if not specified or if there's an error loading the properties. */
    public static boolean graphBuildingEnabled;

    /** Private constructor to prevent instantiation of the {@code Config} class and to give satisfy a Javadoc warning. */
    private Config() {}

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
