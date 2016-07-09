package com.josue.micro.service.registry.service;

import java.util.List;

/**
 * Created by Josue on 30/06/2016.
 */
public class ServiceConfigResponse {
    private final String name;
    private final List<ServiceConfig> instances;

    public ServiceConfigResponse(String name, List<ServiceConfig> instances) {
        this.name = name;
        this.instances = instances;
    }

    public String getName() {
        return name;
    }

    public List<ServiceConfig> getInstances() {
        return instances;
    }

}
