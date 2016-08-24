package com.josue.micro.service.registry.service;

import com.josue.ssr.common.Service;

import java.util.List;

/**
 * Created by Josue on 30/06/2016.
 */
public class ServiceConfigResponse {
    private final String name;
    private final List<Service> instances;

    public ServiceConfigResponse(String name, List<Service> instances) {
        this.name = name;
        this.instances = instances;
    }

    public String getName() {
        return name;
    }

    public List<Service> getInstances() {
        return instances;
    }

}
