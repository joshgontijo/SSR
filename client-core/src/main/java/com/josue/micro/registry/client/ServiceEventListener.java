package com.josue.micro.registry.client;

/**
 * Created by Josue on 10/07/2016.
 */
public interface ServiceEventListener {

    void onConnect(ServiceInstance instance);

    void onDisconnect(ServiceInstance instance);

}
