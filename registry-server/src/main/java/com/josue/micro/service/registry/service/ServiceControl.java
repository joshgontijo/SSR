package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.ServiceException;

import javax.enterprise.context.ApplicationScoped;
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

    static final Set<ServiceConfig> store = ConcurrentHashMap.newKeySet();
//    private static final Map<String, ServiceConfig> store = new ConcurrentHashMap<>();

    //returns all the services, including the disabled ones
    public Map<String, List<ServiceConfig>> getServices(String filter) {
        Map<String, List<ServiceConfig>> collect = store.stream()
                .filter(cfg -> filter == null || cfg.getName().equals(filter))
                .collect(Collectors.groupingBy(ServiceConfig::getName));

        return collect;
    }

    public Collection<ServiceConfig> getServices() {
        return store;
    }

    public ServiceConfig register(String id, ServiceConfig serviceConfig) throws ServiceException {
        if (serviceConfig == null) {
            throw new ServiceException(400, "Service must be provided");
        }
        if (serviceConfig.getName() == null) {
            throw new ServiceException(400, "'name' must be provided");
        }

        if (serviceConfig.getAddress() == null || serviceConfig.getAddress().isEmpty()) {
            throw new ServiceException(400, "'address' must be provided");
        }
        if (serviceConfig.getSince() == null) {
            serviceConfig.setSince(new Date());
        }

        //Copy links from existing services
        List<ServiceConfig> existingServices = store.stream()
                .filter(s -> s.getName().equals(serviceConfig.getName()))
                .collect(Collectors.toList());
        serviceConfig.getLinks().clear();
        if (existingServices != null && !existingServices.isEmpty()) {
            serviceConfig.getLinks().addAll(existingServices.get(0).getLinks());
        }

        deleteUnavailableNodes(serviceConfig.getName());

        serviceConfig.setId(id);
        serviceConfig.setAvailable(true);
        store.add(serviceConfig);

        return serviceConfig;
    }

    public ServiceConfig deregister(String id) throws ServiceException {
        ServiceConfig serviceConfig = store.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst().get();

        if(serviceConfig == null){
            throw new ServiceException(400, "Service not foundSource for session '" + id + "'");
        }

        serviceConfig.setAvailable(false);
        return serviceConfig;

    }

    public ServiceConfig addLink(String id, ServiceConfig target) throws ServiceException {
        ServiceConfig foundSource = store.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .get();

        if(foundSource == null){
            throw new ServiceException(400, "Service not foundSource for session '" + id + "'");
        }

        List<ServiceConfig> foundTargets = store.stream()
                .filter(c -> c.getName().equals(target.getName()))
                .collect(Collectors.toList());

        if (foundTargets.isEmpty()) {
            throw new ServiceException(400, ":: Target service not foundSource: '" + target.getName() + "' ::");
        }

        foundSource.getLinks().add(target.getName());
        return foundSource;
    }

    public void deleteUnavailableNodes(String serviceName) {
        store.removeIf(e -> e.getName().equals(serviceName) && !e.isAvailable());
    }

    private void checkDuplicatedServices(ServiceConfig newService){

    }
}
