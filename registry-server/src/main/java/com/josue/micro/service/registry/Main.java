package com.josue.micro.service.registry;

import org.wildfly.swarm.Swarm;

/**
 * Created by Josue on 21/08/2016.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Swarm swarm = new Swarm();
        swarm.start();
        swarm.deploy();
    }
}
