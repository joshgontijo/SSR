package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.service.Service;

/**
 * Created by Josue on 18/06/2016.
 */
public class Event {
    private EventType type;
    private Service service;

    public Event() {
    }

    public Event(EventType type, Service service) {
        this.type = type;
        this.service = service;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }


    @Override
    public String toString() {
        return "type=" + type + ", service=" + service;
    }
}
