package com.josue.micro.registry.client.discovery;

import com.josue.micro.registry.client.ServiceConfig;

/**
 * Created by Josue on 21/06/2016.
 */
public class ServiceConfigHolder {

    private static ServiceConfig config;

    private ServiceConfigHolder() {
    }

    public static synchronized void initServiceConfig(String name, String serviceAddress) {
        if (config != null) {
            throw new IllegalStateException("Configuration already initialized for this service");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be null or empty");
        }
        if (serviceAddress == null || serviceAddress.isEmpty()) {
            throw new IllegalArgumentException("Service address cannot be null or empty");
        }

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
