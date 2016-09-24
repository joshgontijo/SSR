package com.josue.micro.registry.client.config;

import com.josue.micro.registry.client.discovery.Discovery;
import com.josue.micro.registry.client.discovery.EC2Discovery;
import com.josue.micro.registry.client.discovery.LocalDiscovery;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 26/08/2016.
 */
public class PropertiesManager {

    private final Properties fileProperties;

    public static final String IS_AWS = "server.aws";
    public static final String REGISTRY_HOST = "registry.host";
    public static final String REGISTRY_PORT = "registry.port";

    public static final String SERVICE_HOST = "service.host";
    public static final String SERVICE_PORT = "service.port";
    public static final String USE_HOST = "service.useHostname";

    public static final String ENVIRONMENT_SELECTOR = "ssr.environment";
    public static final String PROPERTIES_FILE_NAME = "registry";

    private static final String DEFAULT_REGISTRY_PORT = "9090";
    private static final String DEFAULT_SERVICE_PORT = "8080";
    private static final String DEFAULT_ENVIRONMENT = "default";
    private static final String DEFAULT_USE_HOST = "true";

    private static final Logger logger = Logger.getLogger(PropertiesManager.class.getName());
    private final Discovery discovery;

    public PropertiesManager() {
        String env = getEnvironment();

        logger.log(Level.INFO, ":: SSR project stage ***** {0} ***** ::", env == null ? "default" : env);
        fileProperties = loadProperties(env);
        discovery = isAws() ? new EC2Discovery() : new LocalDiscovery();
    }

    public int getRegistryPort() {
        String port = getProperty(REGISTRY_PORT);
        port = isEmpty(port) ? DEFAULT_REGISTRY_PORT : port;
        return Integer.parseInt(port);
    }

    public int getServicePort() {
        String port = getProperty(SERVICE_PORT);
        port = isEmpty(port) ? DEFAULT_SERVICE_PORT : port;
        return Integer.parseInt(port);
    }

    public boolean useHostname() {
        String useHost = getProperty(USE_HOST);
        useHost = isEmpty(useHost) ? DEFAULT_USE_HOST : useHost;
        return Boolean.parseBoolean(useHost);
    }

    public String getRegistryHost() {
        return getHost(REGISTRY_HOST);
    }

    public String getServiceHost() {
        return getHost(SERVICE_HOST);
    }

    public String getEnvironment() {
        String env = fromSystemProperties(ENVIRONMENT_SELECTOR);
        return env == null ? DEFAULT_ENVIRONMENT : env;
    }

    private String getHost(String key) {
        String host = getProperty(key);
        if (isEmpty(host)) {
            boolean useHost = useHostname();
            String defaultHost = discovery.resolveHost(useHost);
            fileProperties.put(key, defaultHost);
        }
        return getProperty(key);
    }

    public boolean isAws() {
        return Boolean.parseBoolean(getProperty(IS_AWS));
    }

    private String getProperty(String key) {
        String fromFile = fileProperties.getProperty(key);
        String fromEnv = fromSystemProperties(key);
        return (fromEnv == null || fromEnv.isEmpty()) ? fromFile : fromEnv;
    }

    private Properties loadProperties(String env) {
        Properties properties = new Properties();
        try {
            String fileName = PROPERTIES_FILE_NAME;
            if (env != null && !env.isEmpty() && !env.equals(DEFAULT_ENVIRONMENT)) {
                fileName += "-" + env.trim().toLowerCase();
            }
            fileName += ".properties";

            InputStream is = Configurator.class.getClassLoader().getResourceAsStream(fileName);
            if (is != null) {
                logger.log(Level.INFO, ":: Loading from {0} ::", fileName);
                properties.load(is);
            } else {
                logger.log(Level.INFO, ":: {0} not found ::", fileName);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ":: Error while loading " + PROPERTIES_FILE_NAME + " :: ", ex);
        }

        return properties;
    }

    private String fromSystemProperties(String key) {
        String value = System.getProperty(key);

        if (value == null || value.isEmpty()) {
            //replaces dot by underscore and to uppercase
            //ex: service.port -> SERVICE_PORT
            //so it can be used as System env
            String convertedKeyFormat = key.replace(".", "_").toUpperCase();
            value = System.getenv(convertedKeyFormat);
        }
        return value;
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }


}
