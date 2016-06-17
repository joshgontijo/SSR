package com.josue.micro.jee.register;

import com.josue.micro.registry.client.RegistryException;
import com.josue.micro.registry.client.ServiceConfig;
import com.josue.micro.registry.client.ServiceProviderClient;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
@ApplicationScoped
public class ServiceRegister {

    private static final List<String> services = new ArrayList<>();

    private static final Logger logger = Logger.getLogger(ServiceRegister.class.getName());
    private ServiceProviderClient client;

    private ServiceConfig registeredService;

    @Resource
    private ManagedScheduledExecutorService mses;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object arg) {
        if (!services.isEmpty()) {
            logger.log(Level.INFO, ":: Loading environment properties ::");
            client = new ServiceProviderClient();
            register();
            scheduleHeartbeat();
        } else {
            logger.log(Level.INFO, ":: No services found ::");
        }
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.deregister(registeredService.getId());
        }
    }

    private void register() {
        for (String service : services) {
            logger.log(Level.INFO, ":: Registering service {0} ::", service);
            try {
                registeredService = client.register(service);
            } catch (RegistryException e) {
                throw new RuntimeException(":: Could not register service '" + service + "'", e);
            }
        }
    }

    private void scheduleHeartbeat() {
        mses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    client.heartbeat(registeredService.getId());
                } catch (RegistryException e) {
                    logger.log(Level.SEVERE, ":: Error while sending heartbeat, " + e.getMessage() + " ::");
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public static void addService(String serviceName) {
        services.add(serviceName);
    }

}
