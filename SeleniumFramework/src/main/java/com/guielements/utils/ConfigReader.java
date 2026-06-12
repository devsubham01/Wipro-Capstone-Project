package com.guielements.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;

    static {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            properties = new Properties();
            properties.load(fis);
            fis.close();
            log.info("Config loaded successfully");
        } catch (IOException e) {
            log.error("Config file not found: " + e.getMessage());
            throw new RuntimeException("config.properties not found");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
