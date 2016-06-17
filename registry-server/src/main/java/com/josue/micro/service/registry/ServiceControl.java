package com.josue.micro.service.registry;

import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.josue.micro.service.registry.rest.ServiceResource;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Josue on 15/06/2016.
 */
@ApplicationScoped
public class ServiceControl {

    private static final Logger logger = Logger.getLogger(ServiceResource.class.getName());

    private static final String HEARTBEAT_ENV_KEY = "service.default.lease";
    private static final int DEFAULT_SERVICE_LEASE_TIME = 60;
    private int defaultLeaseTime = DEFAULT_SERVICE_LEASE_TIME;

    @Inject
    private IMap<String, ServiceConfig> cache;


    @PostConstruct
    public void init() {
        String property = System.getProperty(HEARTBEAT_ENV_KEY);
        if (property != null && !property.matches("\\d+")) {
            logger.info(":: SETTING HEARTBEAT PERIOD TO " + property + "ms ::");
            defaultLeaseTime = Integer.valueOf(property);
        } else {
            logger.info(":: LEASE TIME NOT PROVIDED, USING DEFAULT " + DEFAULT_SERVICE_LEASE_TIME + "s ::");
        }
    }

    public Map<String, Collection<ServiceConfig>> getServices() {
        return mapped();
    }

    public ServiceConfig register(ServiceConfig serviceConfig) throws ServiceException {
        if (serviceConfig == null) {
            throw new ServiceException(400, "Service must be provided");
        }
        if (serviceConfig.getName() == null) {
            throw new ServiceException(400, "'name' must be provided");
        }

        if (serviceConfig.getAddress() == null || serviceConfig.getAddress().isEmpty()) {
            throw new ServiceException(400, "'address' must be provided");
        }
        if (serviceConfig.getLeaseTime() == null || serviceConfig.getLeaseTime() <= 0) {
            serviceConfig.setLeaseTime(defaultLeaseTime);
        }

        String uuid = UUID.randomUUID().toString();
        Date now = new Date();

        serviceConfig.setId(uuid.substring(uuid.lastIndexOf("-") + 1, uuid.length()));
        serviceConfig.setLastCheck(now);
        serviceConfig.setSince(now);

        cache.put(serviceConfig.getId(), serviceConfig);

        logger.info(":: ADDING " + serviceConfig.toString() + " ::");

        return serviceConfig;
    }

    public Collection<ServiceConfig> getServicesForName(String serviceName) {
        return mapped().get(serviceName);
    }

    public void deregister(String id) {
        cache.remove(id);
    }

    public ServiceConfig heartbeat(String id) throws ServiceException {
        if (!cache.containsKey(id)) {
            throw new ServiceException(404, "Service not found with id '" + id + "'");
        }
        cache.submitToKey(id, new EntryProcessor<String, ServiceConfig>() {
            @Override
            public Object process(Map.Entry<String, ServiceConfig> entry) {
                ServiceConfig valueMap = entry.getValue();
                valueMap.setLastCheck(new Date());
                entry.setValue(valueMap);
                return null;
            }

            @Override
            public EntryBackupProcessor getBackupProcessor() {
                return null;
            }
        });

        return cache.get(id);
    }

    private Map<String, Collection<ServiceConfig>> mapped() {
        Map<String, Collection<ServiceConfig>> computed = new HashMap<>();

        cache.forEach((s, service) -> {
            long diff = (System.currentTimeMillis() - service.getLastCheck().getTime()) / 1000;
            if (diff > defaultLeaseTime) {
                logger.info(":: REMOVING " + service.toString() + "... R.I.P. ::");
                cache.remove(service.getId());
            } else {
                String serviceName = service.getName();
                if (!computed.containsKey(serviceName)) {
                    computed.put(serviceName, new ArrayList<>());
                }

                computed.get(serviceName).add(service);
            }
        });
        return computed;
    }

}
