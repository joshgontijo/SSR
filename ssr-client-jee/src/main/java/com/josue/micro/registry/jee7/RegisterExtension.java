package com.josue.micro.registry.jee7;

import com.josue.micro.registry.client.config.Configurator;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.ws.rs.ApplicationPath;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
public class RegisterExtension implements Extension {

    private static final Logger logger = Logger.getLogger(RegisterExtension.class.getName());

    private boolean enableClient;
    private String clientName;

    private boolean enableDiscovery;
    private String serviceName;
    private String appRoot;

    <T> void processClientAnnotatedType(@Observes @WithAnnotations(EnableClient.class) ProcessAnnotatedType<T> type) {

        String className = type.getAnnotatedType().getJavaClass().getName();

        clientName = type.getAnnotatedType().getAnnotation(EnableClient.class).name();
        enableClient = true;
        logger.log(Level.INFO, ":: Found SSR client: {0} with name '{1}' ::",
                new Object[]{className, clientName});

        if (clientName == null || clientName.isEmpty()) {
            logger.warning(":: Name not found for client, default will be used ::");
        }
    }

    <T> void processServiceAnnotatedType(@Observes @WithAnnotations(EnableDiscovery.class) ProcessAnnotatedType<T> type) {

        String className = type.getAnnotatedType().getJavaClass().getName();
        ApplicationPath jaxrsApp = type.getAnnotatedType().getJavaClass().getAnnotation(ApplicationPath.class);
        if (jaxrsApp == null) {
            logger.log(Level.SEVERE, ":: Annotated class '{0}' is not a JAXRS application root, " +
                    "service will not be available for discovery ::", className);
            return;
        }

        appRoot = jaxrsApp.value();

        serviceName = type.getAnnotatedType().getAnnotation(EnableDiscovery.class).name();
        enableDiscovery = true;
        logger.log(Level.INFO, " :: Found SSR registry aware service: {0} with name '{1}' on path '{2}' ::",
                new Object[]{className, serviceName, appRoot});

    }

    public void load(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        if (!enableDiscovery && !enableClient) {
            logger.log(Level.SEVERE, " :: No client nor service have been found, SSR won't start ::");
            return;
        }

        String name = serviceName;
        if (enableDiscovery && enableClient) {
            logger.log(Level.INFO, " :: Ignoring '{0}' since '{1}' has already been provided ::", new Object[]{clientName, serviceName});
        } else if (!enableDiscovery) {
            name = clientName;
        }

        Configurator.initService(name, appRoot, enableClient, enableDiscovery);

        Bean<?> registerBean = beanManager.getBeans(CDIBootstrap.class).iterator().next();
        beanManager.getReference(registerBean, registerBean.getBeanClass(), beanManager.createCreationalContext(registerBean)).toString();
    }


}