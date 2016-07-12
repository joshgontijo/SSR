package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.service.ServiceInstance;

/**
 * Created by Josue on 18/06/2016.
 */
public class Event {
    enum Type {
        CONNECTED, DISCONNECTED, SERVICE_USAGE
    }

    private Type type;
    private ServiceInstance service;

    public Event() {
    }

    public Event(Type type, ServiceInstance instance) {
        this.type = type;
        this.service = instance;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ServiceInstance getService() {
        return service;
    }

    public void setService(ServiceInstance service) {
        this.service = service;
    }


    @Override
    public String toString() {
        return "type=" + type + ", service=" + service;
    }
}
