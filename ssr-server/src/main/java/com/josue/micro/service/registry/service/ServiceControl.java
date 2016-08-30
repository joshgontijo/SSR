package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.Service;
import com.josue.micro.service.registry.ServiceException;
import com.josue.ssr.common.Instance;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 15/06/2016.
 */
@ApplicationScoped
public class ServiceControl {

    static final Map<String, Service> store = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(ServiceControl.class.getName());
//    private static final Map<String, ServiceConfig> store = new ConcurrentHashMap<>();

    //returns a copy of all the services, including the disabled ones
    public Service getService(String name) throws ServiceException {
        Service service = store.get(name);
        if (service == null) {
            throw new ServiceException(404, "Service not found for name '" + name + "'");
        }
        return service;
    }

    public Set<Service> getServices() {
        return new HashSet<>(store.values());
    }

    public Instance register(String service, Instance instance) throws ServiceException {
        if (instance == null) {
            throw new ServiceException(400, "Invalid instance");
        }
        if (instance.getAddress() == null || instance.getAddress().trim().isEmpty()) {
            throw new ServiceException(400, "'address' must be provided");
        }
        if (instance.getId() == null || instance.getId().trim().isEmpty()) {
            throw new ServiceException(400, "'id' must be provided");
        }
        if (instance.getSince() == null) {
            instance.setSince(new Date());
        }
        instance.setName(service);
        instance.setState(Instance.State.UP);

        if (!store.containsKey(service)) {
            store.put(service, new Service(service));
        }
        Service serviceConfig = store.get(service);

        serviceConfig.addInstance(instance);
        logger.log(Level.INFO, ":: New service registered {0} ::", instance);

        return instance;
    }

    public Instance updateInstanceState(String instanceId, Instance.State newState) throws ServiceException {
        Optional<Instance> first = store.values().stream()
                .flatMap(l -> l.getInstances().stream())
                .filter(i -> i.getId().equals(instanceId))
                .findFirst();

        if (!first.isPresent()) {
            throw new ServiceException(400, "Service not foundSource for session '" + instanceId + "'");
        }

        Instance instance = first.get();
        instance.updateInstanceState(newState);
        return instance;
    }

    public Service addLink(String client, String target) throws ServiceException {
        Service targetService = store.get(target);
        Service sourceService = store.get(client);

        if (targetService == null) {
            throw new ServiceException(400, "Service " + target + " not found");
        }
        if (sourceService == null) {
            throw new ServiceException(400, "Service " + client + " not found");
        }

        targetService.getLinks().add(client);
        return targetService;
    }

}
