package com.josue.micro.service.registry.cache;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.josue.micro.service.registry.ServiceConfig;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import java.util.logging.Logger;

/**
 * Created by Josue on 09/06/2016.
 */
@ApplicationScoped
public class CacheProducer {

    private static final Logger logger = Logger.getLogger(CacheProducer.class.getName());

    private static final String CACHE_NAME = "services-cache";

    private HazelcastInstance hazelcast;
    private IMap<String, ServiceConfig> cache;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object args) {
        hazelcast = Hazelcast.newHazelcastInstance();
        logger.info(":: INITIALISING CACHE ::");
        cache = hazelcast.getMap(CACHE_NAME);
    }

    @PreDestroy
    public void destroy() {
        logger.info(":: SHUTTING DOWN CACHE ::");
        hazelcast.shutdown();
    }

    @Produces
    public IMap<String, ServiceConfig> producesCache() {
        return cache;
    }

}
