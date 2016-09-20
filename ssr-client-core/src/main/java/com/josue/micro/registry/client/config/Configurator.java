package com.josue.micro.registry.client.config;

import com.josue.ssr.common.Instance;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Josue on 21/06/2016.
 */
public class Configurator {

    private static final Logger logger = Logger.getLogger(Configurator.class.getName());

    private static final PropertiesManager propertyManager = new PropertiesManager();
    private static Instance instance;

    private Configurator() {
    }

    public static synchronized void initService(String name, String appRoot, boolean clientEnabled, boolean enableDiscovery) {
        logger.info("##############################################");
        logger.info("##### BOOTSTRAPING SSR SERVICE DISCOVERY #####");
        logger.info("##############################################");


        String serviceAddress = getServiceUrl(appRoot);
        serviceAddress = verifyProtocol(serviceAddress);

        instance = new Instance();
        instance.setClient(clientEnabled);
        instance.setDiscoverable(enableDiscovery);
        instance.setSince(new Date());
        instance.setAddress(serviceAddress);
        instance.setName(name);
        instance.setState(Instance.State.UP);
    }


    public static synchronized Instance getCurrentInstance() {
        if (instance == null) {
            throw new IllegalStateException("Configuration not initialised yet");
        }
        return instance;
    }

    public static synchronized boolean isInitialised() {
        return instance != null;
    }

    public static String getRegistryUrl() {
        String host = propertyManager.getRegistryHost();
        int port = propertyManager.getRegistryPort();

        host = host.substring(host.length() - 1).equals("/") ?
                host.substring(0, host.length() - 1)
                : host;
        host = host.replaceFirst("http://", "");
        host = host.replaceFirst("https://", "");


        return host + ":" + port;
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

    private static String verifyProtocol(String address) {
        if (!address.startsWith("http://")
                && !address.startsWith("https://")
                && !address.startsWith("ws://")) {

            return "http://" + address;
        }
        return address;
    }
}
