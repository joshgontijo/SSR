package com.josue.micro.registry.client;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Josue on 09/06/2016.
 */
public class ServiceConfig implements Serializable {

    private final String name;
    private final Set<String> links = new HashSet<>();
    private final Set<ServiceInstance> instances = new HashSet<>();

    public ServiceConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<String> getLinks() {
        return links;
    }

    public Set<ServiceInstance> getInstances() {
        return instances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceConfig)) return false;

        ServiceConfig that = (ServiceConfig) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

