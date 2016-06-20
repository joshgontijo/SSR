package com.josue.micro.registry.client.ws;

import com.josue.micro.registry.client.ServiceConfig;
import com.josue.micro.registry.client.ServiceStore;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
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
        session.addMessageHandler(new MessageHandler.Whole<Event>() {
            @Override
            public void onMessage(Event event) {
                logger.log(Level.INFO, ":: New Event: {0} ::", event);
                switch (event.getType()) {
                    case CONNECTED:
                        store.addService(event.getService().getName(), event.getService());
                        break;
                    case DISCONNECTED:
                        store.removeService(event.getService().getId());
                        break;
                    default:
                        logger.log(Level.WARNING, ":: Event {0} not implemented ::", event.getType());
                }
            }
        });

        logger.log(Level.INFO, ":: Sending connection event ::");
        session.getAsyncRemote().sendObject(new Event(Event.Type.CONNECTED, serviceConfig));
    }

    @Override
    public void onError(Session session, Throwable thr) {
        logger.log(Level.SEVERE, "Error handling event", thr);
    }
}
