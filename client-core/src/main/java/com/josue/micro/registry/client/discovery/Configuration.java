package com.josue.micro.registry.client.discovery;

import com.josue.micro.registry.client.ServiceInstance;

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
    private static final String PROPERTIES_FILE_NAME = "registry.properties";
    private static final String SERVICE_URL_KEY = "service.url";
    private static final String REGISTRY_URL_KEY = "registry.url";
    private static final String DEFAULT_REGISTRY_URL = "http://localhost:9000";
    private static final String FORWARD_SLASH = "/";
    private static Properties fileProperties = new Properties();
    private static String registryUrl;
    private static ServiceInstance service;

    private Configuration() {
    }

    public static synchronized void initServiceConfig(String name, String appRoot) {
        if (service != null) {
            throw new IllegalStateException("Configuration already initialized for this service");
        }

        loadProperties();

        String serviceUrl = getProperty(SERVICE_URL_KEY);

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

        service = new ServiceInstance();
        service.setSince(new Date());
        service.setAddress(serviceAddress);
        service.setServiceName(name);
    }

    public static synchronized ServiceInstance getServiceConfig() {
        if (service == null) {
            throw new IllegalStateException("Service service not initialised yet");
        }
        return service;
    }

    public static synchronized boolean isInitialised() {
        return service != null;
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

        registryUrl = getProperty(REGISTRY_URL_KEY);
        if (registryUrl == null) {
            logger.info(":: +" + REGISTRY_URL_KEY + " not found, using default registry URL: " + DEFAULT_REGISTRY_URL + " ::");
            registryUrl = DEFAULT_REGISTRY_URL;
        }
    }

    private static String getProperty(String key) {
        String fromFile = fileProperties.getProperty(key);
        String fromEnv = fromSystemProperties(key);
        return (fromEnv == null || fromEnv.isEmpty()) ? fromFile : fromEnv;
    }

    private static String fromSystemProperties(String key) {
        logger.log(Level.INFO, ":: Loading registry URL ::");
        String propertyValue = System.getProperty(key);

        if (propertyValue == null || propertyValue.isEmpty()) {
            propertyValue = System.getenv(key);
        }
        return propertyValue;
    }

    public static String getRegistryUrl() {
        return registryUrl;
    }
}
