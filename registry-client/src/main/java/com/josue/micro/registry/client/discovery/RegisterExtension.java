package com.josue.micro.registry.client.discovery;

import com.josue.micro.registry.client.ServiceRegister;

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

    <T> void processAnnotatedType(@Observes @WithAnnotations(EnableDiscovery.class) ProcessAnnotatedType<T> type) {

        String className = type.getAnnotatedType().getJavaClass().getName();
        ApplicationPath jaxrsApp = type.getAnnotatedType().getJavaClass().getAnnotation(ApplicationPath.class);
        if (jaxrsApp == null) {
            logger.log(Level.SEVERE, ":: Annotated class {0} is not a JAXRS application root ::", className);
            return;
        }

        String rootPath = jaxrsApp.value();

        String serviceName = type.getAnnotatedType().getAnnotation(EnableDiscovery.class).serviceName();
        logger.log(Level.INFO, " :: Found registry aware service: {0} with name {1} ::", new Object[]{
                className, serviceName
        });

        ServiceNameHolder.setServiceName(serviceName);
    }

    public void load(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        Bean<?> registerBean = beanManager.getBeans(ServiceRegister.class).iterator().next();
        beanManager.getReference(registerBean, registerBean.getBeanClass(), beanManager.createCreationalContext(registerBean)).toString();
    }
}
