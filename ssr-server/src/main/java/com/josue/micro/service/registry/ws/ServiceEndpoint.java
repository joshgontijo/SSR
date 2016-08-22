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
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 17/06/2016.
 */
@ServerEndpoint(value = "/services/{serviceName}",
        decoders = ServiceInstanceEncoder.class,
        encoders = ServiceInstanceEncoder.class)
public class ServiceEndpoint {

    private static final Logger logger = Logger.getLogger(ServiceEndpoint.class.getName());

    @Inject
    private ServiceControl control;

    @Inject
    private SessionStore sessionStore;

    @OnOpen
    public void onOpen(@PathParam("serviceName") String serviceName, Session session) {
        logger.log(Level.INFO, ":: Session open, service {0}, id {1} ::", new Object[]{serviceName, session.getId()});
    }

    @OnMessage
    public void onMessage(@PathParam("serviceName") String serviceName, ServiceInstance serviceInstance, Session session) throws ServiceException {
        logger.log(Level.INFO, ":: Connection event received {0} ::", serviceInstance);
        if (serviceInstance == null) {
            throw new ServiceException(400, "Invalid ServiceInstance, null state");
        }

        sessionStore.addSession(serviceName, session);
        serviceInstance.setId(extractSessionId(session));
        ServiceInstance registered = control.register(serviceName, serviceInstance.getId(), serviceInstance);
        sessionStore.pushInstanceState(registered);
    }

    @OnClose
    public void onClose(@PathParam("serviceName") String serviceName, Session session, CloseReason closeReason) throws ServiceException {
        logger.log(Level.INFO, ":: Service disconnected, service {0}, id {1}, reason {2} ::", new Object[]{serviceName, session, closeReason.getReasonPhrase()});
        sessionStore.removeSession(serviceName, session);
        ServiceInstance updated = control.updateInstanceState(extractSessionId(session), ServiceInstance.State.DOWN);
        sessionStore.pushInstanceState(updated);
    }

    @OnError
    public void onError(@PathParam("serviceName") String serviceName, Session session, Throwable t) {
        if (t instanceof IOException) {
            logger.log(Level.WARNING, "Session {0} interrupted, service {1} may have been shutdown, see below", new Object[]{session.getId(), serviceName});
            logger.log(Level.WARNING, "Session was closed because: ", t.getMessage());
        } else {
            logger.log(Level.SEVERE, "Error receiving event", t);
        }
    }

    private String extractSessionId(Session session) {
        String id = session.getId().length() >= 8 ? session.getId().substring(0, 8) : session.getId();
        return id.toLowerCase();
    }

}
