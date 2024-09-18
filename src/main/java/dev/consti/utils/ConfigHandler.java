package dev.consti.utils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigHandler {

    private static final Properties properties = new Properties();
    private static final String ConfigFile = "config.properties";

    static {
        try {

            File configFile = new File(ConfigFile);
            if (!configFile.exists()) {

                try (InputStream input = ConfigHandler.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
                     FileOutputStream output = new FileOutputStream(configFile)) {
                     
                    if (input == null) {
                        throw new IllegalArgumentException("Config file not found in resources");
                    }
                    
                    
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }

            }

            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
