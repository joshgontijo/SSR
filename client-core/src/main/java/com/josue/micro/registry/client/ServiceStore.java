package com.josue.micro.registry.client;

import com.josue.ssr.common.Instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Josue on 19/06/2016.
 */
public class ServiceStore implements ServiceEventListener {

    private static final Object LOCK = new Object();

    private static final Map<String, Set<Instance>> store = new ConcurrentHashMap<>();
    private Set<String> links = new HashSet<>();

    //TODO implement
//    private static final Queue<Event> eventBuffer = new ConcurrentLinkedDeque<>();
//    private static final int eventBufferSize = 1; //TODO configurable

    public Instance get(String serviceName) {
        return get(serviceName, Strategy.roundRobin());
    }

    public Set<String> getServices() {
        return store.keySet();
    }

    public Instance get(String serviceName, Strategy strategy) {
        if (!store.containsKey(serviceName)) {
            return null;
        }
        Set<Instance> instances = store.get(serviceName);
        if (instances == null) {
            return null;
        }

        Instance apply = strategy.apply(new ArrayList<>(instances));

        sendLink(apply.getName());

//        sentStats(instances);

        return apply;
    }

    public void addService(Instance instance) {
        synchronized (LOCK) {
            if (!store.containsKey(instance.getName())) {
                store.put(instance.getName(), new HashSet<>());
            }
            store.get(instance.getName()).add(instance);
        }
    }

    public void removeService(Instance instance) {
        synchronized (LOCK) {
            if (store.containsKey(instance.getName())) {
                store.get(instance.getName()).remove(instance);
            }
            if (store.get(instance.getName()).isEmpty()) {
                store.remove(instance.getName());
            }
        }
    }

    private void sendLink(String target) {
        if (!links.contains(target)) {
            //send link
        }
    }

    private void clear() {
        synchronized (LOCK) {
            store.clear();
            links.clear();
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
    public void onConnect(Instance instance) {
        addService(instance);
    }

    @Override
    public void onDisconnect(Instance instance) {
        removeService(instance);
    }

    @Override
    public void newSession() {
        clear();
    }

}
