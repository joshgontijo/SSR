package com.josue.micro.registry.client.ws;

import com.josue.micro.registry.client.ServiceConfig;

/**
 * Created by Josue on 18/06/2016.
 */
public class Event {

    private EventType type;
    private ServiceConfig service;

    public Event() {
    }

    public Event(EventType type, ServiceConfig service) {
        this.type = type;
        this.service = service;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        if (type != event.type) return false;
        return service != null ? service.equals(event.service) : event.service == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (service != null ? service.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "type=" + type +
                ", service=" + service;
    }
}
