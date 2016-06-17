package com.josue.micro.jee.register;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
public class RegisterExtension implements Extension {

    private static final Logger logger = Logger.getLogger(RegisterExtension.class.getName());

    <T> void processAnnotatedType(@Observes @WithAnnotations(EnableDiscovery.class) ProcessAnnotatedType<T> type) {

        String serviceName = type.getAnnotatedType().getAnnotation(EnableDiscovery.class).serviceName();
        logger.log(Level.INFO, " :: Found registry aware service: {0} with name {1} ::", new Object[]{
                type.getAnnotatedType().getJavaClass().getName(), serviceName
        });

        ServiceRegister.addService(serviceName);
    }
}
