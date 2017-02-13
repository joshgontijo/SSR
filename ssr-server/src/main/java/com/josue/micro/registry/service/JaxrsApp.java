package com.josue.micro.registry.service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Josue on 13/02/2016.
 */
@ApplicationPath("api")
public class JaxrsApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(ServiceResource.class, InstancesResource.class));
    }
}
