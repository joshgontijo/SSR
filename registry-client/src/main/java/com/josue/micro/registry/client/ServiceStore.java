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

    public ServiceConfig getAny(String serviceName) {
        if (!store.containsKey(serviceName)) {
            return null;
        }
        return store.get(serviceName).get(0);
    }

    //TODO implement get strategy(round robin) etc
    public ServiceConfig getAny(String serviceName, String strategy) {
        throw new RuntimeException(":: Not implemented yet ::");
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
