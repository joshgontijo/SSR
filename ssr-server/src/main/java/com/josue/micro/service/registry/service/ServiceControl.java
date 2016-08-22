package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
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

    static final Map<String, Service> store = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(ServiceControl.class.getName());
//    private static final Map<String, ServiceConfig> store = new ConcurrentHashMap<>();

    //returns a copy of all the services, including the disabled ones
    public Set<Service> getServices(String filter) {
        Set<Service> collect = store.values().stream()
                .filter(cfg -> filter == null || cfg.getName().equals(filter))
                .collect(Collectors.toSet());

        return new HashSet<>(collect);
    }

    public ServiceInstance register(String service, String instanceId, ServiceInstance instance) throws ServiceException {
        if (!store.containsKey(service)) {
            store.put(service, new Service(service));
        }
        Service serviceConfig = store.get(service);

        if (instance.getAddress() == null || instance.getAddress().isEmpty()) {
            throw new ServiceException(400, "'address' must be provided");
        }
        if (instance.getSince() == null) {
            instance.setSince(new Date());
        }
        instance.setId(instanceId);
        instance.setServiceName(service);
        instance.setState(ServiceInstance.State.UP);

        serviceConfig.addInstance(instance);

        return instance;
    }

    public ServiceInstance updateInstanceState(String instanceId, ServiceInstance.State newState) throws ServiceException {
        Optional<ServiceInstance> first = store.values().stream()
                .flatMap(l -> l.getInstances().stream())
                .filter(i -> i.getId().equals(instanceId))
                .findFirst();

        if (!first.isPresent()) {
            throw new ServiceException(400, "Service not foundSource for session '" + instanceId + "'");
        }

        ServiceInstance instance = first.get();
        instance.updateInstanceState(newState);
        return instance;
    }

    //TODO implement
//    public ServiceConfig addLink(String id, ServiceConfig target) throws ServiceException {
//        Optional<ServiceConfig> foundSourceOpt = store.stream()
//                .filter(s -> s.getInstances().stream().map(ServiceInstance::getId).filter(id::equals).findFirst().isPresent())
//                .findFirst();
//
//        if (!foundSourceOpt.isPresent()) {
//            throw new ServiceException(400, "Service not foundSource for session '" + id + "'");
//        }
//
//        ServiceConfig foundSource = foundSourceOpt.get();
//
//        Optional<ServiceConfig> foundTarget = store.stream()
//                .filter(target::equals).findFirst();
//
//        if (!foundTarget.isPresent()) {
//            throw new ServiceException(400, ":: Target service not foundSource: '" + target.getName() + "' ::");
//        }
//
//        foundSource.getLinks().add(target.getName());
//        return foundSource;
//    }

}
