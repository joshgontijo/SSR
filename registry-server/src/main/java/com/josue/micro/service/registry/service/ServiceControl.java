package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
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

    //returns a copy of all the services, including the disabled ones
    public Set<ServiceConfig> getServices(String filter) {
        Set<ServiceConfig> collect = store.stream()
                .filter(cfg -> filter == null || cfg.getName().equals(filter))
                .collect(Collectors.toSet());

        return new HashSet<>(collect);
    }

    public Set<ServiceConfig> getServices() {
        return getServices(null);
    }

    public ServiceConfig register(String id, ServiceConfig serviceConfig) throws ServiceException {
        if (serviceConfig == null) {
            throw new ServiceException(400, "Service must be provided");
        }
        if (serviceConfig.getName() == null || serviceConfig.getName().isEmpty()) {
            throw new ServiceException(400, "Service name not provided");
        }
        if (serviceConfig.getInstances() == null || serviceConfig.getInstances().isEmpty()) {
            throw new ServiceException(400, "Instances not provided");
        }

        deleteUnavailableNodes(serviceConfig.getName());
        for (ServiceInstance si : serviceConfig.getInstances()) {
            if (si.getAddress() == null || si.getAddress().isEmpty()) {
                throw new ServiceException(400, "'address' must be provided");
            }
            if (si.getSince() == null) {
                si.setSince(new Date());
            }

            si.setId(id);
            si.setAvailable(true);

            Set<ServiceConfig> collect = store.stream().filter(s -> s.getName().equals(serviceConfig.getName())).collect(Collectors.toSet());
            if (!collect.isEmpty()) {
                collect.iterator().next().getInstances().add(si);
            } else {
                ServiceConfig config = new ServiceConfig(serviceConfig.getName());
                config.getInstances().add(si);
                store.add(config);
            }
        }

        return serviceConfig;
    }

    public ServiceConfig deregister(String id) throws ServiceException {
        Optional<ServiceConfig> serviceConfigOpt = store.stream()
                .filter(s -> s.getInstances().stream()
                        .filter(i -> i.getId().equals(id))
                        .findFirst()
                        .isPresent())
                .findFirst();


        if (!serviceConfigOpt.isPresent()) {
            throw new ServiceException(400, "Service not foundSource for session '" + id + "'");
        }

        ServiceConfig serviceConfig = serviceConfigOpt.get();
        serviceConfig.getInstances().forEach(i -> {
            if (i.getId().equals(id)) {
                i.setAvailable(false);
                i.setSince(null);
                i.setDownSince(new Date());
            }
        });

        return serviceConfig;

    }

    public ServiceConfig addLink(String id, ServiceConfig target) throws ServiceException {
        Optional<ServiceConfig> foundSourceOpt = store.stream()
                .filter(s -> s.getInstances().stream().map(ServiceInstance::getId).filter(id::equals).findFirst().isPresent())
                .findFirst();

        if (!foundSourceOpt.isPresent()) {
            throw new ServiceException(400, "Service not foundSource for session '" + id + "'");
        }

        ServiceConfig foundSource = foundSourceOpt.get();

        Optional<ServiceConfig> foundTarget = store.stream()
                .filter(target::equals).findFirst();

        if (!foundTarget.isPresent()) {
            throw new ServiceException(400, ":: Target service not foundSource: '" + target.getName() + "' ::");
        }

        foundSource.getLinks().add(target.getName());
        return foundSource;
    }

    public void deleteUnavailableNodes(String serviceName) {
        store.stream()
                .filter(s -> s.getName().equals(serviceName))
                .collect(Collectors.toSet())
                .forEach(s -> s.getInstances().removeIf(e -> !e.isAvailable()));
        store.removeIf(s -> s.getInstances().isEmpty());
    }

}
