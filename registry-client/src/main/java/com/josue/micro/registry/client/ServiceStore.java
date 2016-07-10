package com.josue.micro.registry.client;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Josue on 19/06/2016.
 */
@ApplicationScoped
public class ServiceStore {

    private static final Map<String, Set<ServiceConfig>> store = new ConcurrentHashMap<>();

    public ServiceConfig get(String serviceName) {
        return get(serviceName, Strategy.first());
    }

    public ServiceConfig get(String serviceName, Strategy strategy) {
        if (!store.containsKey(serviceName)) {
            return null;
        }
        Set<ServiceConfig> configs = store.get(serviceName);
        if (configs == null || configs.isEmpty()) {
            return null;
        }
        return strategy.apply(new ArrayList<>(configs));
    }

    public void addService(ServiceConfig service) {
        String name = service.getName();
        if (!store.containsKey(name)) {
            store.put(name, new HashSet<>());
        }
        store.get(name).add(service);
    }

    public void removeService(String id) {
        store.values().forEach(c -> c.removeIf(cfg -> cfg.getId().equals(id)));
    }

    protected void updateService(ServiceConfig config) {
        //overwrite old one, since we only use name + address as hascode
        store.get(config.getName()).add(config);
    }

}
