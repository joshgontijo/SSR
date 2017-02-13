package com.josue.micro.registry.client;

import com.josue.micro.registry.client.config.Configurator;
import com.josue.ssr.common.EndpointPath;

import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
public class ServiceRegister implements Runnable {

    private static final Logger logger = Logger.getLogger(ServiceRegister.class.getName());

    private static final Object LOCK = new Object();
    private static final AtomicInteger retryCounter = new AtomicInteger();
    private static final int RETRY_INTERVAL = 10;//in seconds
    public static boolean shutdownSignal = false;
    private Session session;
    private ServiceStore store;
    private ScheduledExecutorService executorService;


    public ServiceRegister(ServiceStore store, ScheduledExecutorService executorService) {
        this.store = store;
        this.executorService = executorService;
    }

    public void init() {
        synchronized (LOCK) {
            logger.info("##############################################");
            logger.info("##### BOOTSTRAPING SSR SERVICE DISCOVERY #####");
            logger.info("##############################################");
            if (Configurator.isInitialised()) {
                register();
            } else {
                logger.log(Level.INFO, ":: No services found ::");
            }
        }
    }

    public void register() {
        deregister();

        synchronized (LOCK) {
            if (!shutdownSignal && (session == null || !session.isOpen())) {
                retryCounter.set(0);
                executorService.schedule(this, 5, TimeUnit.SECONDS);
            }
        }
    }

    public void shutdown() {
        synchronized (LOCK) {
            logger.info(":: Shutting down ::");
            shutdownSignal = true;
        }
        deregister();
    }

    public void deregister() {
        synchronized (LOCK) {
            try {
                if (session != null && session.isOpen()) {
                    logger.log(Level.INFO, ":: Closing WS session ::");
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Service disconnected"));
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, ":: Error while closing the session ::", e);
            }
        }
    }

    @Override
    public void run() {
        synchronized (LOCK) {
            try {
                String registryUrl =
                        "ws://" +
                                Configurator.getRegistryUrl() + EndpointPath.REGISTRY_PATH +
                                "/" + Configurator.getCurrentInstance().getName();

                WebSocketContainer container = ContainerProvider.getWebSocketContainer();

                logger.log(Level.INFO, ":: Trying to connect to {0} ::", new Object[]{registryUrl, retryCounter.incrementAndGet()});

                ServiceClientEndpoint endpoint = new ServiceClientEndpoint(this, store);

                session = container.connectToServer(endpoint, new URI(registryUrl));

                logger.log(Level.INFO, ":: Connected ! ::", session.getId());

            } catch (Exception e) {
                logger.log(Level.WARNING, ":: Could not connect to the registry, retrying in {0}s ::", RETRY_INTERVAL);
                executorService.schedule(this, RETRY_INTERVAL, TimeUnit.SECONDS);
            }
        }
    }
}
