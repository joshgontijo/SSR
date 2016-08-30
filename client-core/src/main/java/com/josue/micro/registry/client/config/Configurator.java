package com.josue.micro.registry.client.config;

import com.josue.ssr.common.Instance;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Josue on 21/06/2016.
 */
public class Configurator {

    private static final Logger logger = Logger.getLogger(Configurator.class.getName());

    private static final String REGISTRY_PATH = "services";
    private static final PropertiesManager propertyManager = new PropertiesManager();
    private static Instance instance;

    private Configurator() {
    }

    public static synchronized void initService(String name, String appRoot, boolean clientEnabled, boolean enableDiscovery) {
        logger.info("##############################################");
        logger.info("##### BOOTSTRAPING SSR SERVICE DISCOVERY #####");
        logger.info("##############################################");


        String serviceAddress = getServiceUrl(appRoot);

        instance = new Instance();
        instance.setClient(clientEnabled);
        instance.setDiscoverable(enableDiscovery);
        instance.setSince(new Date());
        instance.setAddress(serviceAddress);
        instance.setName(name);
        instance.setState(Instance.State.UP);
    }


    public static synchronized Instance getServiceConfig() {
        if (instance == null) {
            throw new IllegalStateException("Configuration not initialised yet");
        }
        return instance;
    }

    public static synchronized boolean isInitialised() {
        return instance != null;
    }

    public static String getRegistryUrl() {
        String registryUrl = propertyManager.getRegistryHost() +
                ":" + propertyManager.getRegistryPort();
        String urlSeparator = registryUrl.endsWith("/") ? "" : "/";
        if (registryUrl.startsWith("ws")) {
            //do nothing
        } else if (registryUrl.startsWith("http")) {
            registryUrl = registryUrl.replaceFirst("http", "ws");
        } else if (registryUrl.startsWith("https")) {
            registryUrl = registryUrl.replaceFirst("https", "ws");
        } else {//protocol not provided
            registryUrl = "ws://" + registryUrl;
        }
        return registryUrl + urlSeparator + REGISTRY_PATH + "/" + Configurator.getServiceConfig().getName();
    }


    private static String getServiceUrl(String appRoot) {
        String serviceHost = propertyManager.getServiceHost();
        int servicePort = propertyManager.getServicePort();

        if (serviceHost == null || serviceHost.isEmpty()) {
            throw new IllegalArgumentException("Service address cannot be null or empty");
        }

        String serviceUrl = serviceHost + ":" + servicePort;

        serviceUrl = serviceUrl.endsWith("/") ?
                serviceUrl.substring(0, serviceUrl.length() - 1) :
                serviceUrl;

        return serviceUrl + "/" + getRootPath(appRoot);
    }

    private static String getRootPath(String appRoot) {
        appRoot = appRoot == null ? "" : appRoot;
        if (!appRoot.isEmpty()) {
            appRoot = appRoot.startsWith("/") ? appRoot.substring(1, appRoot.length()) : appRoot;
            appRoot = appRoot.endsWith("/") ? appRoot.substring(0, appRoot.length() - 1) : appRoot;
        }
        return appRoot;
    }
}
