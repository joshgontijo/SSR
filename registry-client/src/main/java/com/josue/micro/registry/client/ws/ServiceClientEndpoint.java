package com.josue.micro.registry.client.ws;

import com.josue.micro.registry.client.ServiceRegister;
import com.josue.micro.registry.client.ServiceStore;
import com.josue.micro.registry.client.discovery.ServiceConfigHolder;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
@ClientEndpoint(encoders = EventEncoder.class, decoders = EventEncoder.class)
public class ServiceClientEndpoint  {

    private static final Logger logger = Logger.getLogger(ServiceClientEndpoint.class.getName());
    private ServiceStore store;
    private ServiceRegister register;

    public ServiceClientEndpoint(ServiceStore store, ServiceRegister register) {
        this.store = store;
        this.register = register;
    }

//    @Inject
//    private ServiceStore store;
//
//    @Inject
//    private ServiceRegister register;
//
//    @PostConstruct
//    public void init(){
//        logger.info("----------------------------");
//    }


    @OnOpen
    public void onOpen(Session session) {
        logger.log(Level.INFO, ":: Sending connection event ::");
        session.getAsyncRemote().sendObject(new Event(Event.Type.CONNECTED, ServiceConfigHolder.getServiceConfig()));
    }

    @OnMessage
    public void onMessage(Event event, Session session) {
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


//    @Override
//    public void onOpen(Session session, EndpointConfig endpoint) {
//        session.addMessageHandler(new MessageHandler.Whole<Event>() {
//            @Override
//            public void onMessage(Event event) {
//                logger.log(Level.INFO, ":: New Event: {0} ::", event);
//                switch (event.getType()) {
//                    case CONNECTED:
//                        store.addService(event.getService().getName(), event.getService());
//                        break;
//                    case DISCONNECTED:
//                        store.removeService(event.getService().getId());
//                        break;
//                    default:
//                        logger.log(Level.WARNING, ":: Event {0} not implemented ::", event.getType());
//                }
//            }
//        });
//
//        logger.log(Level.INFO, ":: Sending connection event ::");
//        session.getAsyncRemote().sendObject(new Event(Event.Type.CONNECTED, ServiceConfigHolder.getServiceConfig()));
//    }
//
//    @Override
//    public void onClose(Session session, CloseReason closeReason) {
//        logger.log(Level.SEVERE, ":: Server closed the connection, reason: {0} ::", closeReason);
//        register.register();
//    }
//
//    @Override
//    public void onError(Session session, Throwable thr) {
//        logger.log(Level.SEVERE, "Error handling event", thr);
//    }
}
