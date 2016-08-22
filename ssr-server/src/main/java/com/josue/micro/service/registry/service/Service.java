package com.josue.micro.service.registry.service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Josue on 09/06/2016.
 */
public class Service implements Serializable {

    private final Set<String> links = new HashSet<>();
    private final Map<String, ServiceInstance> instances = new ConcurrentHashMap<>();
    private String name;

    public Service() {
    }

    public Service(String name) {
        this.name = name;
    }

    public Set<String> getLinks() {
        return links;
    }

    public Set<ServiceInstance> getInstances() {
        return new HashSet<>(instances.size());
    }

    public boolean containsInstance(String instanceId) {
        return instances.containsKey(instanceId);
    }

    public ServiceInstance addInstance(ServiceInstance instance) {
        ServiceInstance existent = null;
        for (Map.Entry<String, ServiceInstance> entry : instances.entrySet()) {
            if (entry.getValue().equals(instance)) {
                existent = entry.getValue();
            }
        }

        //already exists and is not UP, remove it
        if (existent != null && !ServiceInstance.State.UP.equals(existent.getState())) {
            instances.remove(existent.getId());
        }
        return instances.put(instance.getId(), instance);
    }

    public synchronized ServiceInstance removeInstance(String instanceId) {
        if (!containsInstance(instanceId)) {
            return null;
        }
        return instances.remove(instanceId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;

        Service that = (Service) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

