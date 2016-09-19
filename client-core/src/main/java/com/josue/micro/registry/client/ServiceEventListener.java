package com.josue.micro.registry.client;

import com.josue.ssr.common.Instance;

/**
 * Created by Josue on 10/07/2016.
 */
public interface ServiceEventListener {

    void onConnect(Instance instance);

    void onDisconnect(Instance instance);

    void newSession();
}
