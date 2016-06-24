package com.josue.micro.registry.client;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Josue on 19/06/2016.
 */
@ApplicationScoped
public class ServiceStore {

    private static final Map<String, List<ServiceConfig>> store = new ConcurrentHashMap<>();

    public ServiceConfig get(String serviceName) {
        return get(serviceName, Strategy.any());
    }

    public ServiceConfig get(String serviceName, Strategy strategy) {
        if (!store.containsKey(serviceName)) {
            return null;
        }
        List<ServiceConfig> configs = store.get(serviceName);
        if (configs == null || configs.isEmpty()) {
            return null;
        }
        return strategy.apply(configs);
    }

    public void addService(String key, ServiceConfig value) {
        if (!store.containsKey(key)) {
            store.put(key, new ArrayList<>());
        }
        store.get(key).add(value);
    }

    public void removeService(String id) {
        store.values().forEach(c -> c.removeIf(cfg -> cfg.getId().equals(id)));
    }

}
