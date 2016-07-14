package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.service.ServiceConfig;

/**
 * Created by Josue on 18/06/2016.
 */
public class Event {
    private EventType type;
    private ServiceConfig service;

    public Event() {
    }

    public Event(EventType type, ServiceConfig serviceConfig) {
        this.type = type;
        this.service = serviceConfig;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
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
