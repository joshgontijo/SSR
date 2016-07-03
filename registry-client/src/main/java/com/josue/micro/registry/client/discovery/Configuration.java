package com.josue.micro.registry.client.discovery;

import com.josue.micro.registry.client.ServiceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 21/06/2016.
 */
public class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class.getName());

    private static Properties fileProperties = new Properties();

    private static final String PROPERTIES_FILE_NAME = "registry.properties";
    private static final String SERVICE_URL_SUFFIX = ".url";
    private static final String REGISTRY_URL = "registry.url";

    private static String registryUrl;
    private static String serviceUrl;

    private static final String FORWARD_SLASH = "/";

    private static ServiceConfig config;

    private Configuration() {

    }

    public static synchronized void initServiceConfig(String name, String appRoot) {
        if (config != null) {
            throw new IllegalStateException("Configuration already initialized for this service");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be null or empty");
        }

        loadProperties();

        serviceUrl = getProperty(name + SERVICE_URL_SUFFIX);

        if (serviceUrl == null || serviceUrl.isEmpty()) {
            throw new IllegalArgumentException("Service address cannot be null or empty");
        }

        serviceUrl = serviceUrl.endsWith(FORWARD_SLASH) ?
                serviceUrl.substring(0, serviceUrl.length() - 1) :
                serviceUrl;

        appRoot = appRoot == null ? "" : appRoot;
        if (!appRoot.isEmpty()) {
            appRoot = appRoot.startsWith(FORWARD_SLASH) ? appRoot.substring(1, appRoot.length()) : appRoot;
            appRoot = appRoot.endsWith(FORWARD_SLASH) ? appRoot.substring(0, appRoot.length() - 1) : appRoot;
        }

        String serviceAddress = serviceUrl + FORWARD_SLASH + appRoot;

        config = new ServiceConfig();
        config.setName(name);
        config.setAddress(serviceAddress);
        config.setSince(new Date());

    }

    public static synchronized ServiceConfig getServiceConfig() {
        if (config == null) {
            throw new IllegalStateException("Service config not initialised yet");
        }
        return new ServiceConfig(config);
    }

    public static synchronized boolean isInitialised() {
        return config != null;
    }

    private static void loadProperties() {
        InputStream is = Configuration.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
        if (is == null) {
            logger.log(Level.INFO, ":: {0} not found ::", PROPERTIES_FILE_NAME);
        } else {
            try {
                fileProperties.load(is);
            } catch (IOException e) {
                logger.log(Level.SEVERE, ":: Error loading file :: ", e);
            }
        }

        registryUrl = getProperty(REGISTRY_URL);
    }

    private static String getProperty(String key) {
        String fromFile = fileProperties.getProperty(key);
        String fromEnv = fromSystemProperties(key);
        String property = (fromEnv == null || fromEnv.isEmpty()) ? fromFile : fromEnv;
        if (property == null) {
            logger.log(Level.SEVERE, ":: Value for {0} not found on {1} file or environment variable ::",
                    new Object[]{key, PROPERTIES_FILE_NAME});
        }
        return property;
    }

    private static String fromSystemProperties(String key) {
        logger.log(Level.INFO, ":: Loading registry URL ::");
        String propertyValue = System.getProperty(key);

        if (propertyValue == null || propertyValue.isEmpty()) {
            propertyValue = System.getenv(key);
        }

        return propertyValue;
    }

    public static String getServiceUrl() {
        return serviceUrl;
    }

    public static String getRegistryUrl() {
        return registryUrl;
    }
}
