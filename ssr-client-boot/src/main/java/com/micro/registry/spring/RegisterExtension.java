package com.micro.registry.spring;

import com.josue.micro.registry.client.ServiceRegister;
import com.josue.micro.registry.client.ServiceStore;
import com.josue.micro.registry.client.config.Configurator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextStartedEvent;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
@ComponentScan(basePackages = "com.josue.micro.registry")
@org.springframework.context.annotation.Configuration
public class RegisterExtension implements ApplicationListener {

    private static final Logger logger = Logger.getLogger(RegisterExtension.class.getName());

    private ServiceRegister serviceRegister;

    @Autowired
    private ServiceStore store;

    private ScheduledExecutorService mses;

    private static final String DEFAULT_BOOT_CONTEXT_PATH = "/";

    private boolean enableClient;
    private String clientName;

    private boolean enableDiscovery;
    private String serviceName;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextStartedEvent) {
            String name = serviceName;
            if (enableDiscovery && enableClient) {
                logger.log(Level.INFO, " :: Ignoring '{0}' since '{1}' has already been provided ::", new Object[]{clientName, serviceName});
            } else if (!enableDiscovery) {
                name = clientName;
            }

            Configurator.initService(name, DEFAULT_BOOT_CONTEXT_PATH, enableClient, enableDiscovery);
        }
    }

    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessor() {
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                Class<?> clazz = bean.getClass();
                if (clazz.isAnnotationPresent(EnableDiscovery.class)) {
                    logger.log(Level.INFO, " :: Found registry aware service: {0} ::",
                            new Object[]{clazz.getName()});
                    serviceName = clazz.getAnnotation(EnableDiscovery.class).name();
                    enableDiscovery = true;
                }

                if (clazz.isAnnotationPresent(EnableClient.class)) {
                    logger.log(Level.INFO, " :: Found SSR client: {0} ::",
                            new Object[]{clazz.getName()});
                    clientName = clazz.getAnnotation(EnableClient.class).name();
                    enableClient = true;
                }

                return bean;
            }
        };
    }

    public void init() {
        this.mses = Executors.newScheduledThreadPool(2);

        serviceRegister = new ServiceRegister(store, mses);
        serviceRegister.init();
    }

    @PreDestroy
    public void shutdown() {
        if (serviceRegister != null) {
            serviceRegister.shutdown();
        }
        if (mses != null) {
            mses.shutdown();
        }
    }

    @Bean
    public ServiceStore store() {
        return new ServiceStore();
    }

}