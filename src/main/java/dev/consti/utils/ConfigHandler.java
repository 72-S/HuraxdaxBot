package dev.consti.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigHandler {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigHandler.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
