package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Josue on 15/06/2016.
 */
@ApplicationScoped
public class ServiceControl {

    private static final Logger logger = Logger.getLogger(ServiceControl.class.getName());

    private static final Map<Session, ServiceConfig> store = new ConcurrentHashMap<>();

    public Map<String, List<ServiceConfig>> getServices(String filter) {
        Map<String, List<ServiceConfig>> collect = store.values().stream()
                .filter(cfg -> filter == null || cfg.getName().equals(filter))
                .collect(Collectors.groupingBy(ServiceConfig::getName));

        return collect;
    }

    public Set<Session> getSessions() {
        return store.keySet();
    }

    public Collection<ServiceConfig> getServices() {
        return store.values();
    }

    public ServiceConfig register(Session session, ServiceConfig serviceConfig) throws ServiceException {
        if (serviceConfig == null) {
            throw new ServiceException(400, "Service must be provided");
        }
        if (serviceConfig.getName() == null) {
            throw new ServiceException(400, "'name' must be provided");
        }

        if (serviceConfig.getAddress() == null || serviceConfig.getAddress().isEmpty()) {
            throw new ServiceException(400, "'address' must be provided");
        }
        if(serviceConfig.getSince() == null){
            serviceConfig.setSince(new Date());
        }

        serviceConfig.setId(session.getId());


        store.put(session, serviceConfig);

        logger.info(":: Service registered " + serviceConfig + " ::");

        return serviceConfig;
    }

    public ServiceConfig deregister(Session session) {
        ServiceConfig removed = store.remove(session);
        logger.info(":: Service deregistered " + removed + " ::");
        return removed;

    }

}
