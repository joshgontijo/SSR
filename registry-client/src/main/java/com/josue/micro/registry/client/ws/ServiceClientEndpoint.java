package com.josue.micro.registry.client.ws;

import com.josue.micro.registry.client.ServiceEventListener;
import com.josue.micro.registry.client.ServiceRegister;
import com.josue.micro.registry.client.discovery.Configuration;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
@ClientEndpoint(encoders = EventEncoder.class, decoders = EventEncoder.class)
public class ServiceClientEndpoint {

    private static final Logger logger = Logger.getLogger(ServiceClientEndpoint.class.getName());

    private static final List<ServiceEventListener> listeners = Collections.synchronizedList(new ArrayList<>());

    private final ServiceRegister register;

    public ServiceClientEndpoint(ServiceRegister register) {
        this.register = register;
    }

    @OnOpen
    public void onOpen(Session session) {
        logger.log(Level.INFO, ":: Sending connection event ::");
        session.getAsyncRemote().sendObject(new Event(EventType.CONNECTED, Configuration.getServiceConfig()));
    }

    @OnMessage
    public void onMessage(Event event, Session session) {
        logger.log(Level.INFO, ":: New Event: {0} ::", event);

        switch (event.getType()) {
            case CONNECTED:
                for (ServiceEventListener listener : listeners) {
                    listener.onConnect(event);
                }
                break;
            case DISCONNECTED:
                for (ServiceEventListener listener : listeners) {
                    listener.onDisconnect(event);
                }
                break;
            case SERVICE_USAGE:
                for (ServiceEventListener listener : listeners) {
                    listener.onServiceUsage(event);
                }
                break;
            default:
                logger.log(Level.WARNING, ":: Event {0} not implemented ::", event.getType());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        if (!ServiceRegister.shutdownSignal) {
            logger.log(Level.SEVERE, ":: Connection closed, reason: {0} ::", closeReason);
            register.register();
        } else {
            logger.log(Level.INFO, ":: Client initiated shutdown proccess, not reconnecting ::", closeReason);
        }

    }

    @OnError
    public void onError(Session session, Throwable thr) {
        logger.log(Level.SEVERE, "Error handling event", thr);
    }

    public void addListener(ServiceEventListener listener) {
        listeners.add(listener);
    }
}

