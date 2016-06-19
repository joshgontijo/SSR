package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.ServiceConfig;

/**
 * Created by Josue on 18/06/2016.
 */
public class Event {
    enum Type{
        CONNECT, DISCONNECT, ERROR
    }

    private Type type;
    private ServiceConfig service;

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
}
