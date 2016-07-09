package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.service.ServiceConfig;
import com.josue.micro.service.registry.service.ServiceControl;
import com.josue.micro.service.registry.ServiceException;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 17/06/2016.
 */
@ServerEndpoint(value = "/registry",
        decoders = EventEncoder.class,
        encoders = EventEncoder.class)
public class ServiceEndpoint {

    private static final Logger logger = Logger.getLogger(ServiceEndpoint.class.getName());

    @Inject
    private ServiceControl control;

    @OnOpen
    public void onOpen(Session session) {
        logger.log(Level.INFO, ":: Session open, id {0} ::", session.getId());
    }

    @OnMessage
    public void onMessage(Event event, Session session) throws ServiceException {
        logger.log(Level.INFO, ":: Event received {0} ::", event);
        ServiceConfig registered = control.register(session, event.getService());
        //send to all other services except current session
        sendEvent(session, new Event(Event.Type.CONNECTED, registered));

        //send all already connected services to current session
        control.getServices().stream()
                .forEach(connected -> session.getAsyncRemote().sendObject(new Event(Event.Type.CONNECTED, connected)));

        logger.log(Level.INFO, ":: New service registered, session {0}, service {1} ::", new Object[]{session.getId(), String.valueOf(event.getService())});
        logger.log(Level.INFO, ":: Connected services {0} ::", control.getSessions().size());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        ServiceConfig removed = control.deregister(session);
        sendEvent(session, new Event(Event.Type.DISCONNECTED, removed));
        logger.log(Level.INFO, ":: Session {0} closed because of {1} ::", new Object[]{session.getId(), closeReason});
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

    private void sendEvent(Session currentSession, Event event) {
        control.getSessions().stream()
                .filter(s -> s.isOpen() && !s.equals(currentSession))
                .forEach(s -> s.getAsyncRemote().sendObject(event));
    }
}
