package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.service.ServiceConfig;

/**
 * Created by Josue on 18/06/2016.
 */
public class Event {
    enum Type {
        CONNECTED, DISCONNECTED
    }

    private Type type;
    private ServiceConfig service;

    public Event() {
    }

    public Event(Type type, ServiceConfig service) {
        this.type = type;
        this.service = service;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ServiceConfig getService() {
        return service;
    }

    public void setService(ServiceConfig service) {
        this.service = service;
    }


    @Override
    public String toString() {
        return "type=" + type + ", service=" + service;
    }
}
