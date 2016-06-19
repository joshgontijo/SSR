package com.josue.micro.service.registry;

import com.hazelcast.core.IMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.Session;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Josue on 15/06/2016.
 */
@ApplicationScoped
public class ServiceControl {

    private static final Logger logger = Logger.getLogger(ServiceControl.class.getName());

    @Inject
    private IMap<Session, ServiceConfig> cache;


    public Map<String, List<ServiceConfig>> getServices(String filter) {
        Map<String, List<ServiceConfig>> collect = cache.values().stream()
                .filter(cfg -> filter == null || cfg.getName().equals(filter))
                .collect(Collectors.groupingBy(ServiceConfig::getName));

        return collect;
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

        serviceConfig.setId(session.getId());
        serviceConfig.setSince(new Date());

        cache.put(session, serviceConfig);

        logger.info(":: ADDED " + serviceConfig.toString() + " ::");

        return serviceConfig;
    }

    public Collection<ServiceConfig> getServicesForName(String serviceName) {
        return mapped().get(serviceName);
    }

    public void deregister(Session session) {
        cache.remove(session);
    }


    private Map<String, Collection<ServiceConfig>> mapped() {
        Map<String, Collection<ServiceConfig>> computed = new HashMap<>();
//        cache.entrySet().stream().map(service -> service.getValue().getName()).collect(Collectors.groupingBy(ServiceConfig::getName));
//
//        cache.forEach((s, service) -> {
//            String serviceName = service.getName();
//            if (!computed.containsKey(serviceName)) {
//                computed.put(serviceName, new ArrayList<>());
//            }
//
//            computed.get(serviceName).add(service);
//        });
        return computed;
    }

}
