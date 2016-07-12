package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.List;
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
    public Set<ServiceConfig> getServices(String filter) {
        Set<ServiceConfig> collect = store.stream()
                .filter(cfg -> filter == null || cfg.getName().equals(filter))
                .collect(Collectors.toSet());

        return collect;
    }

    public Set<ServiceConfig> getServices() {
        return store;
    }

    public ServiceInstance register(String id, ServiceConfig serviceConfig) throws ServiceException {
        if (serviceConfig == null) {
            throw new ServiceException(400, "Service must be provided");
        }
        if(serviceConfig.getInstances() == null || serviceConfig.getInstances().isEmpty()){
            throw new ServiceException(400, "Instances not provided");
        }

        if (serviceConfig.getAddress() == null || serviceConfig.getAddress().isEmpty()) {
            throw new ServiceException(400, "'address' must be provided");
        }
        if (serviceConfig.getSince() == null) {
            serviceConfig.setSince(new Date());
        }

        deleteUnavailableNodes(serviceConfig.getName());

        serviceConfig.setId(id);
        serviceConfig.setAvailable(true);
        Set<ServiceConfig> collect = store.stream().filter(s -> s.getName().equals(serviceConfig.getName())).collect(Collectors.toSet());
        if(!collect.isEmpty()){
          collect.iterator().next().getInstances().add(serviceConfig);
        }
        else{
            ServiceConfig config = new ServiceConfig(serviceConfig.getName());
            config.getInstances().add(serviceConfig);
            store.add(config);
        }

        return serviceConfig;
    }

    public ServiceInstance deregister(String id) throws ServiceException {
        ServiceInstance instance = store.stream()
                .flatMap(s -> s.getInstances().stream())
                .filter(s -> s.getId().equals(id))
                .findFirst().get();

        if(instance == null){
            throw new ServiceException(400, "Service not foundSource for session '" + id + "'");
        }

        instance.setAvailable(false);
        return instance;

    }

    public ServiceConfig addLink(String id, ServiceInstance target) throws ServiceException {
        ServiceConfig foundSource = store.stream()
                .filter(s -> s.getInstances().stream().map(ServiceInstance::getId).filter(id::equals).findFirst().isPresent())
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
        store.forEach(s -> s.getInstances().removeIf(e -> e.getName().equals(serviceName) && !e.isAvailable()));
        store.removeIf(s -> s.getInstances().isEmpty());
    }

}
