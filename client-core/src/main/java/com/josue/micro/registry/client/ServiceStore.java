package com.josue.micro.registry.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Josue on 19/06/2016.
 */
public class ServiceStore implements ServiceEventListener {

    private static final Map<String, Set<ServiceInstance>> store = new ConcurrentHashMap<>();

    //TODO implement
//    private static final Queue<Event> eventBuffer = new ConcurrentLinkedDeque<>();
//    private static final int eventBufferSize = 1; //TODO configurable

    public ServiceInstance get(String serviceName) {
        return get(serviceName, Strategy.roundRobin());
    }

    public ServiceInstance get(String serviceName, Strategy strategy) {
        if (!store.containsKey(serviceName)) {
            return null;
        }
        Set<ServiceInstance> instances = store.get(serviceName);
        if (instances == null) {
            return null;
        }

        ServiceInstance apply = strategy.apply(new ArrayList<>(instances));

//        sentStats(instances);

        return apply;
    }

    public void addService(ServiceInstance instance) {
        if (!store.containsKey(instance.getServiceName())) {
            store.put(instance.getServiceName(), new HashSet<>());
        }
        store.get(instance.getServiceName()).add(instance);
    }

    public void removeService(ServiceInstance instance) {
        if (store.containsKey(instance.getServiceName())) {
            store.get(instance.getServiceName()).remove(instance);
        }
        if (store.get(instance.getServiceName()).isEmpty()) {
            store.remove(instance.getServiceName());
        }
    }

//    private void sentStats(ServiceConfig config) {
//        if (session != null && session.isOpen()) {
//            eventBuffer.add(new Event(EventType.SERVICE_USAGE, config));
//
//            if (eventBuffer.size() == eventBufferSize) {
//                //for loop, since it will iterate a fixed ammount of times
//                //regardless some other thread adding more elements
//                for (int i = 0; i <= eventBuffer.size(); i++) {
//                    Event poll = eventBuffer.poll();
//                    if (poll != null) {
//                        session.getAsyncRemote().sendObject(poll);
//                    }
//                }
//            }
//
//        }
//    }

    @Override
    public void onConnect(ServiceInstance instance) {
        addService(instance);
    }

    @Override
    public void onDisconnect(ServiceInstance instance) {
        removeService(instance);
    }

}
