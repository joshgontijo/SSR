package com.josue.micro.registry.client.ws;

import com.josue.micro.registry.client.ServiceConfig;
import com.josue.micro.registry.client.ServiceStore;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
@ClientEndpoint(
        decoders = EventEncoder.class,
        encoders = EventEncoder.class)
public class ServiceClientEndpoint extends Endpoint {

    private static final Logger logger = Logger.getLogger(ServiceClientEndpoint.class.getName());

    private final ServiceStore store;
    private final ServiceConfig serviceConfig;

    public ServiceClientEndpoint(ServiceStore store, ServiceConfig serviceConfig) {
        this.store = store;
        this.serviceConfig = serviceConfig;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpoint) {
        session.getAsyncRemote().sendObject(new Event(Event.Type.CONNECT, serviceConfig));
    }

    @Override
    public void onError(Session session, Throwable thr) {
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        session.getAsyncRemote().sendObject(new Event(Event.Type.DISCONNECT, serviceConfig));
    }

    @OnMessage
    public void onMessage(final Event event) {
        logger.info(":: New Event " + event);
        switch (event.getType()) {
            case CONNECT:
                store.addService(event.getService().getName(), event.getService());
            case DISCONNECT:
                store.removeService(event.getService().getId());
            case ERROR:
                logger.warning(":: Error while handling data on server, error ::");
            default:
                logger.warning(":: Event not implemented ::");
        }
    }
}
