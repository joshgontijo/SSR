package com.josue.micro.service.registry.ws;


import com.josue.ssr.common.Instance;

import javax.inject.Singleton;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Josue on 22/08/2016.
 */
@Singleton
public class SessionStore {

    private static final Map<String, Set<Session>> clients = new ConcurrentHashMap<>();

    public synchronized void addSession(String service, Session session) {
        if (!clients.containsKey(service)) {
            clients.put(service, new HashSet<>());
        }
        clients.get(service).add(session);
    }

    public synchronized void removeSession(String service, Session session) {
        if (!clients.containsKey(service)) {
            return;
        }
        clients.get(service).remove(session);
    }

    /**
     * Sends the newly registered service to all already available instances
     * If the new instance is not discoverable, nothing will happen
     * @param registered
     */
    public void pushInstanceState(Instance registered) {
        if (!registered.isDiscoverable()) {//do not send non discoverable service to the clients
            return;
        }
        //send to all other services except current session
        clients.entrySet().stream()
                .flatMap(l -> l.getValue().stream())
                .forEach(s -> {
                    if (s.isOpen()) {
                        s.getAsyncRemote().sendObject(registered); //TODO filter by dependency ?
                    }
                });
    }

}
