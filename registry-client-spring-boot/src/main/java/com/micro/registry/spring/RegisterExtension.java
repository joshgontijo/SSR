package com.micro.registry.spring;

import com.josue.micro.registry.client.ServiceRegister;
import com.josue.micro.registry.client.ServiceStore;
import com.josue.micro.registry.client.discovery.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

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
public class RegisterExtension {

    private static final Logger logger = Logger.getLogger(RegisterExtension.class.getName());

    private ServiceRegister serviceRegister;

    @Autowired
    private ServiceStore store;

    private ScheduledExecutorService mses;

    private static final String DEFAULT_BOOT_CONTEXT_PATH = "/";
    private static final String BOOT_APPLICATION_PROPERTIES = "application.properties";

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
                    String serviceName = clazz.getAnnotation(EnableDiscovery.class).name();

                    Configuration.initServiceConfig(serviceName, DEFAULT_BOOT_CONTEXT_PATH, BOOT_APPLICATION_PROPERTIES); //TODO get root context path
                    init();
                }

                return bean;
            }
        };
    }

    public void init() {
        //TODO managed ?
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
    public ServiceStore store(){
        return new ServiceStore();
    }
}