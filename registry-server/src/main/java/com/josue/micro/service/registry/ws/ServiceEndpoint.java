package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.ServiceException;
import com.josue.micro.service.registry.service.ServiceControl;
import com.josue.micro.service.registry.service.ServiceInstance;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Josue on 17/06/2016.
 */
@ServerEndpoint(value = "/registry",
        decoders = EventEncoder.class,
        encoders = EventEncoder.class)
public class ServiceEndpoint {

    private static final Logger logger = Logger.getLogger(ServiceEndpoint.class.getName());

    private static final Set<Session> connectedSessions = ConcurrentHashMap.newKeySet();

    @Inject
    private ServiceControl control;

    @OnOpen
    public void onOpen(Session session) {
        logger.log(Level.INFO, ":: Session open, id {0} ::", session.getId());
    }

    @OnMessage
    public void onMessage(Event event, Session session) throws ServiceException {
        logger.log(Level.INFO, ":: Event received {0} ::", event);

        switch (event.getType()) {
            case CONNECTED:
                ServiceInstance registered = control.register(session.getId(), event.getService());
                handleConnectEvent(session, registered);
                break;
            case DISCONNECTED:
                //TODO handle ?
                break;
            case SERVICE_USAGE:
                control.addLink(session.getId(), event.getService());
                break;
            default:
                throw new RuntimeException(":: Event type not recognized : '" + event.getType() + "' ::");
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) throws ServiceException {
        handleDisconnectEvent(session, closeReason);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        if (t instanceof IOException) {
            logger.log(Level.WARNING, "Session {0} interrupted, service may have been shutdown, see below", session.getId());
            logger.log(Level.WARNING, "Session was closed because: ", t);
        } else {
            logger.log(Level.SEVERE, "Error receiving event", t);
        }
    }

    private void handleConnectEvent(Session session, ServiceInstance registered) {
        //send to all other services except current session
        sendEvent(session, new Event(Event.Type.CONNECTED, registered));

        //send all already connected services to current session

        List<ServiceInstance> collect = control.getServices().stream().flatMap(s -> s.getInstances().stream()).collect(Collectors.toList());

        collect.stream()
                .filter(s -> s.isAvailable()) //!! ONLY AVAILABLE HERE
                .forEach(connected -> session.getAsyncRemote().sendObject(new Event(Event.Type.CONNECTED, connected)));

        logger.log(Level.INFO, ":: New service registered, session {0}, service {1} ::", new Object[]{session.getId(), String.valueOf(registered)});
        connectedSessions.add(session);
        logger.log(Level.INFO, ":: Connected services {0} ::", connectedSessions.size());
    }

    private void handleDisconnectEvent(Session session, CloseReason closeReason) throws ServiceException {
        ServiceInstance removed = control.deregister(session.getId());
        sendEvent(session, new Event(Event.Type.DISCONNECTED, removed));
        logger.log(Level.INFO, ":: Session {0} closed because of {1} ::", new Object[]{session.getId(), closeReason});
        connectedSessions.remove(session);
    }

    private void sendEvent(Session currentSession, Event event) {
        connectedSessions.stream()
                .filter(s -> s.isOpen() && !s.equals(currentSession))
                .forEach(s -> s.getAsyncRemote().sendObject(event));
    }
}
