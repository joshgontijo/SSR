package com.josue.micro.registry.jee7;

import com.josue.micro.registry.client.ServiceRegister;
import com.josue.micro.registry.client.ServiceStore;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Created by Josue on 17/07/2016.
 */
@ApplicationScoped
public class CDIBootstrap {

    private ServiceRegister serviceRegister;

    @Inject
    private ServiceStore store;

    @Resource
    private ManagedScheduledExecutorService mses;

    @PostConstruct
    public void init() {
        serviceRegister = new ServiceRegister(store, mses);
        serviceRegister.init();
    }

    @PreDestroy
    public void shutdown() {
        if (serviceRegister != null) {
            serviceRegister.shutdown();
        }
    }

    @Produces
    @ApplicationScoped
    public ServiceStore store() {
        return new ServiceStore();
    }

}
