package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.service.ServiceInstance;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Josue on 22/08/2016.
 */
@ApplicationScoped
public class SessionStore {

    private static final Map<String, Set<Session>> sessions = new ConcurrentHashMap<>();

    public synchronized void addSession(String service, Session session) {
        if (!sessions.containsKey(service)) {
            sessions.put(service, new HashSet<>());
        }
        sessions.get(service).add(session);
    }

    public synchronized void removeSession(String service, Session session) {
        if (!sessions.containsKey(service)) {
            return;
        }
        sessions.get(service).remove(session);
    }

    public void pushInstanceState(ServiceInstance registered) {
        //send to all other services except current session
        sessions.entrySet().stream()
                .flatMap(l -> l.getValue().stream())
                .forEach(s -> {
                    if (s.isOpen()) {
                        s.getAsyncRemote().sendObject(registered); //TODO filter by dependency
                    }
                });
    }

}
