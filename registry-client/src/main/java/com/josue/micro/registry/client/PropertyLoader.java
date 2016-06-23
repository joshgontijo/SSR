package com.josue.micro.registry.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Josue Gontijo.
 */
public class PropertyLoader {

    private Properties fileProperties = new Properties();

    private static final String PROPERTIES_FILE_NAME = "registry.properties";
    private static final String SERVICE_URL = "service.url";
    private static final String REGISTRY_URL = "registry.url";

    private String registryUrl;
    private String serviceUrl;

    private static final Logger logger = Logger.getLogger(PropertyLoader.class.getName());

    private static PropertyLoader INSTANCE;


    private PropertyLoader() {
        InputStream is = PropertyLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
        if (is == null) {
            logger.log(Level.INFO, ":: {0} not found ::", PROPERTIES_FILE_NAME);
        } else {
            try {
                fileProperties.load(is);
            } catch (IOException e) {
                logger.log(Level.SEVERE, ":: Error loading file :: ", e);
            }
        }

        serviceUrl = getProperty(SERVICE_URL);

        if (serviceUrl == null) {
            logger.log(Level.SEVERE, ":: Value for {0} not found on {1} file or environment variable ::",
                    new Object[]{SERVICE_URL, PROPERTIES_FILE_NAME});
        }

        registryUrl = getProperty(REGISTRY_URL);

        if (registryUrl == null) {
            logger.log(Level.SEVERE, ":: Value for {0} not found on {1} file or environment variable ::",
                    new Object[]{REGISTRY_URL, PROPERTIES_FILE_NAME});
        }

    }

    public static synchronized PropertyLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PropertyLoader();
        }
        return INSTANCE;
    }

    private String getProperty(String key){
        String fromEnv = fileProperties.getProperty(key);
        String fromFile = fromSystemProperties(key);
        return !isBlank(fromEnv) ? fromEnv : fromFile;
    }

    private String fromSystemProperties(String key) {
        logger.log(Level.INFO, ":: Loading registry URL ::");
        String propertyValue = System.getProperty(key);

        if (propertyValue == null || propertyValue.isEmpty()) {
            propertyValue = System.getenv(key);
        }

        return propertyValue;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isEmpty();
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }


}
