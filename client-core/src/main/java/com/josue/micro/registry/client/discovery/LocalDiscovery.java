package com.josue.micro.registry.client.discovery;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Josue on 26/08/2016.
 */
public class LocalDiscovery implements Discovery {

    @Override
    public String resolveHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Could not resolce local host address", e);
        }
    }

}
