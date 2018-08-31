package com.nprinting.utils;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtils {

    private PropertiesUtils() {
        throw new RuntimeException("This class should not be instantiated");
    }

    public static Properties loadFromFile(String pathToPropertiesFile) {
        try {
            final Properties properties = new Properties();
            final InputStream propertiesInputStream = new FileInputStream(pathToPropertiesFile);
            properties.load(propertiesInputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
