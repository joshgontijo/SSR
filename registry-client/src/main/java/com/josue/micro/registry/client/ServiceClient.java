package com.josue.micro.registry.client;

import com.josue.micro.registry.client.ws.ServiceClientEndpoint;

import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 17/06/2016.
 */
public class ServiceClient {

    private static final Logger logger = Logger.getLogger(ServiceClient.class.getName());

    private static final String SERVICE_URL = "service.url";

    private static final String REGISTRY_PATH = "registry";
    private static final String REGISTRY_URL_KEY = "registry.url";

    private static Session session;

    private static final ServiceStore serviceStore = new ServiceStore();

    public ServiceClient() {
    }

    public synchronized static void register(String name) {
        if (session != null && session.isOpen()) {
            deregister();
        }

        String registryUrl = getRegistryUrl();
        String serviceUrl = getServiceUrl();
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setName(name);
            serviceConfig.setAddress(serviceUrl);

            Endpoint clientEndpoint = new ServiceClientEndpoint(serviceStore, serviceConfig);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            logger.log(Level.INFO, ":: CONNECTING TO {0} ::", registryUrl);
            session = container.connectToServer(clientEndpoint, new URI(registryUrl));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static void deregister() {
        try {
            session.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, ":: Could not close session ::", e);
        }
    }

    private static String getRegistryUrl() {
        logger.log(Level.INFO, ":: Loading registry URL ::");
        String registryUrl = System.getProperty(REGISTRY_URL_KEY);
        if (registryUrl == null || registryUrl.isEmpty()) {
            throw new IllegalStateException(":: Could not find environment property '" + REGISTRY_URL_KEY + "' ::");
        }
        String urlSeparator = registryUrl.endsWith("/") ? "" : "/";
        return registryUrl + urlSeparator + REGISTRY_PATH;
    }

    private static String getServiceUrl() {
        logger.log(Level.INFO, ":: Loading service URL ::");
        String serviceUrl = System.getProperty(SERVICE_URL);
        if (serviceUrl == null || serviceUrl.isEmpty()) {
            throw new IllegalStateException(":: Could not find environment property '" + SERVICE_URL + "' ::");
        }
        return serviceUrl;
    }
}
