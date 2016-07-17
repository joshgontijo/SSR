package com.josue.micro.registry.client;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Josue on 09/06/2016.
 */
public class ServiceConfig implements Serializable {

    private String name;
    private final Set<String> links = new HashSet<>();
    private final Set<ServiceInstance> instances = new HashSet<>();

    public ServiceConfig() {
    }

    public ServiceConfig(String name) {
        this.name = name;
    }

    public ServiceConfig(ServiceConfig config) {
        this.name = config.name;
        this.links.addAll(config.getLinks());
        this.getInstances().addAll(config.getInstances());
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

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", instances=" + instances +
                ", links=" + links;
    }
}

