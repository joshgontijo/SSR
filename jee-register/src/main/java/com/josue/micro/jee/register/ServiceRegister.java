package com.josue.micro.jee.register;

import com.josue.micro.registry.client.ServiceClient;
import com.josue.micro.registry.client.ws.ServiceClientEndpoint;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
@ApplicationScoped
public class ServiceRegister {

    private static final Logger logger = Logger.getLogger(ServiceRegister.class.getName());

    private static String service;

    private ServiceClientEndpoint client;

    @Resource
    private ManagedScheduledExecutorService mses;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object arg) {
        if (service != null && !service.isEmpty()) {
            ServiceClient.register(service);
        } else {
            logger.log(Level.INFO, ":: No services found ::");
        }
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            ServiceClient.deregister();
        }
    }


    public static void addService(String serviceName) {
        if (service != null && !service.isEmpty()) {
            logger.log(Level.WARNING, ":: Multiple services found: [\'{0}\',\'{1}\'], feature not supported yet ::",
                    new Object[]{service, serviceName});
        }
        service = serviceName;
    }

}
