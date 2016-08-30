package com.josue.micro.service.registry.ws;

import com.josue.micro.service.registry.ServiceException;
import com.josue.micro.service.registry.service.ServiceControl;
import com.josue.ssr.common.Instance;
import com.josue.ssr.common.InstanceEncoder;

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
        decoders = InstanceEncoder.class,
        encoders = InstanceEncoder.class)
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
    public void onMessage(@PathParam("serviceName") String serviceName, Instance instance, Session session) throws ServiceException {
        if (instance == null) {
            throw new ServiceException(400, "Invalid ServiceInstance");
        }

        if (instance.isClient()) {
            sessionStore.addSession(serviceName, session);
        }

        instance.setId(extractSessionId(session));
        Instance registered = control.register(serviceName, instance);
        sessionStore.pushInstanceState(registered);
    }

    @OnClose
    public void onClose(@PathParam("serviceName") String serviceName, Session session, CloseReason closeReason) throws ServiceException {
        logger.log(Level.INFO, ":: Service disconnected, service {0}, id {1}, reason {2} ::", new Object[]{serviceName, extractSessionId(session), closeReason.getReasonPhrase()});
        sessionStore.removeSession(serviceName, session);
        Instance updated = control.updateInstanceState(extractSessionId(session), Instance.State.DOWN);
        sessionStore.pushInstanceState(updated);
    }

    @OnError
    public void onError(@PathParam("serviceName") String serviceName, Session session, Throwable t) {
        if (t instanceof IOException) {
            logger.log(Level.WARNING, "Session {0} interrupted, service '{1}' may have been shutdown", new Object[]{extractSessionId(session), serviceName});
        } else {
            logger.log(Level.SEVERE, "Error receiving event", t);
        }
    }

    private String extractSessionId(Session session) {
        String id = session.getId().length() >= 8 ? session.getId().substring(0, 8) : session.getId();
        return id.toLowerCase();
    }

}
