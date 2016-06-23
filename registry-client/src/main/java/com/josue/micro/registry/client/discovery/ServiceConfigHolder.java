package com.josue.micro.registry.client.discovery;

import com.josue.micro.registry.client.ServiceConfig;

/**
 * Created by Josue on 21/06/2016.
 */
public class ServiceConfigHolder {

    private static final String FORWARD_SLASH = "/";

    private static ServiceConfig config;

    private ServiceConfigHolder() {
    }

    public static synchronized void initServiceConfig(String name, String applicationAddress, String appRoot) {
        if (config != null) {
            throw new IllegalStateException("Configuration already initialized for this service");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be null or empty");
        }
        if (applicationAddress == null || applicationAddress.isEmpty()) {
            throw new IllegalArgumentException("Service address cannot be null or empty");
        }

        applicationAddress = applicationAddress.endsWith(FORWARD_SLASH) ?
                applicationAddress.substring(0, applicationAddress.length() - 1) :
                applicationAddress;

        appRoot = appRoot == null ? "" : appRoot;
        if (!appRoot.isEmpty()) {
            appRoot = appRoot.startsWith(FORWARD_SLASH) ? appRoot.substring(1, appRoot.length()) : appRoot;
            appRoot = appRoot.endsWith(FORWARD_SLASH) ? appRoot.substring(0, appRoot.length() - 1) : appRoot;
        }

        String serviceAddress = applicationAddress + FORWARD_SLASH + appRoot;


        config = new ServiceConfig();
        config.setName(name);
        config.setAddress(serviceAddress);
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
}
