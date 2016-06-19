package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.ServiceControl;
import com.josue.micro.service.registry.ServiceException;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
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
        logger.log(Level.INFO, ":: Session open, sessionId {0}::", session.getId());
    }

    @OnMessage
    public void onMessage(Event message, Session session) throws ServiceException {
        control.register(session, message.getService());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        control.deregister(session);
        logger.log(Level.INFO, ":: Session {0} closed because of {1} ::", new Object[]{session.getId(), closeReason});
    }

    @OnError
    public void onError(Session session, Throwable t) {
        logger.log(Level.INFO, ":: Error {0} closed because of {1} ::", new Object[]{session.getId(), t.getMessage()});

        Event event = new Event();
        event.setType(Event.Type.ERROR);

        try {
            session.getBasicRemote().sendObject(event);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending ERROR event", e);
        }
    }
}
