package com.josue.micro.registry.client;

import com.josue.micro.registry.client.ws.Event;

/**
 * Created by Josue on 10/07/2016.
 */
public interface ServiceEventListener {

    void onConnect(Event event);

    void onDisconnect(Event event);

    void onThisDisconnects();

    void onServiceUsage(Event event);
}
