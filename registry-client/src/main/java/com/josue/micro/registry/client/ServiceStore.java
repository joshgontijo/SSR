package com.josue.micro.registry.client;

import com.josue.micro.registry.client.ws.Event;
import com.josue.micro.registry.client.ws.EventType;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Josue on 19/06/2016.
 */
@ApplicationScoped
public class ServiceStore implements ServiceEventListener {

    private static final Map<String, ServiceConfig> store = new ConcurrentHashMap<>();

    private static final Queue<Event> eventBuffer = new ConcurrentLinkedDeque<>();
    private static final int eventBufferSize = 1; //TODO configurable

    private Session session;

    public ServiceInstance get(String serviceName) {
        return get(serviceName, Strategy.roundRobin());
    }

    public ServiceInstance get(String serviceName, Strategy strategy) {
        if (!store.containsKey(serviceName)) {
            return null;
        }
        ServiceConfig config = store.get(serviceName);
        if (config == null) {
            return null;
        }

        ServiceInstance apply = strategy.apply(new ArrayList<>(config.getInstances()));

        sentStats(config);

        return apply;
    }

    public void addService(ServiceConfig service) {
        store.put(service.getName(), service);
    }

    public void removeService(ServiceConfig service) {
        service.getInstances().removeIf(s -> !s.isAvailable());
        if (service.getInstances().isEmpty()) {
            store.remove(service.getName());
        } else {
            store.put(service.getName(), service);
        }
    }

    protected void updateService(ServiceConfig service) {
        //overwrite old one, since we only use name + address as hascode
        store.put(service.getName(), service);
    }

    private void sentStats(ServiceConfig config) {
        if (session != null && session.isOpen()) {
            eventBuffer.add(new Event(EventType.SERVICE_USAGE, config));

            if (eventBuffer.size() == eventBufferSize) {
                //for loop, since it will iterate a fixed ammount of times
                //regardless some other thread adding more elements
                for (int i = 0; i <= eventBuffer.size(); i++) {
                    Event poll = eventBuffer.poll();
                    if (poll != null) {
                        session.getAsyncRemote().sendObject(poll);
                    }
                }
            }

        }
    }

    protected void setSession(Session session) {
        this.session = session;
    }

    @Override
    public void onConnect(Event event) {
        addService(event.getService());
    }

    @Override
    public void onDisconnect(Event event) {
        removeService(event.getService());
    }

    @Override
    public void onServiceUsage(Event event) {
        updateService(event.getService());
    }
}
