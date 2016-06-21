package com.josue.micro.registry.client.ws;

import com.josue.micro.registry.client.ServiceConfig;
import com.josue.micro.registry.client.ServiceRegister;
import com.josue.micro.registry.client.ServiceStore;

import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
@ClientEndpoint(encoders = EventEncoder.class, decoders = EventEncoder.class)
public class ServiceClientEndpoint extends Endpoint {

    private static final Logger logger = Logger.getLogger(ServiceClientEndpoint.class.getName());

    @Inject
    private ServiceStore store;

    @Inject
    private ServiceConfig serviceConfig;

    @Inject
    private ServiceRegister register;

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

    //using @OnClose annotation will trigger client events, which causes undesired call to register() on shutdown
    @Override
    public void onClose(Session session, CloseReason closeReason) {
        logger.log(Level.SEVERE, ":: Server closed the connection, reason: {0} ::", closeReason);
        register.register();
    }

    @Override
    public void onError(Session session, Throwable thr) {
        logger.log(Level.SEVERE, "Error handling event", thr);
    }
}
