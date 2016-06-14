package com.josue.micro.service.registry;


import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Josue on 09/06/2016.
 */
@Path("services")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServiceResource implements Serializable {

    private static final Logger logger = Logger.getLogger(ServiceResource.class.getName());

    private static final String HEARTBEAT_ENV_KEY = "service.default.leasetTime";
    private static final int DEFAULT_SERVICE_LEASE_TIME = 20;
    private int leaseTime = DEFAULT_SERVICE_LEASE_TIME;

    @Inject
    private IMap<String, ServiceConfig> cache;

    @Context
    private HttpServletRequest request;

    @PostConstruct
    public void init() {
        String property = System.getProperty(HEARTBEAT_ENV_KEY);
        if (property != null && !property.matches("\\d+")) {
            logger.info(":: SETTING HEARTBEAT PERIOD TO " + property + "ms ::");
            leaseTime = Integer.valueOf(property);
        } else {
            logger.info(":: LEASE TIME NOT PROVIDED, USING DEFAULT " + DEFAULT_SERVICE_LEASE_TIME + "s ::");
        }
    }

    @GET
    public Response getServices() {
        return Response.ok(mapped()).build();
    }

    @GET
    @Path("{service}")
    public Response getService(@PathParam("service") String service) {
        Map<String, Collection<ServiceConfig>> mapped = mapped();
        return Response.ok(mapped.get(service)).build();
    }

    @POST
    public Response addService(ServiceConfig serviceConfig) {
        if (serviceConfig == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(simpleJsonMessage("Service not provided")).build();
        }
        if (serviceConfig.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(simpleJsonMessage("'name' not provided"))
                    .build();
        }

        if(serviceConfig.getUrl() == null || serviceConfig.getUrl().isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(simpleJsonMessage("'url' not provided"))
                    .build();
        }

        if(serviceConfig.getPort() == null || serviceConfig.getPort().isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(simpleJsonMessage("'port' not provided"))
                    .build();
        }

        String uuid = UUID.randomUUID().toString();
        serviceConfig.setId(uuid.substring(uuid.lastIndexOf("-") + 1, uuid.length()));
        serviceConfig.setLastCheck(System.currentTimeMillis());
        serviceConfig.setLeaseTime(leaseTime);

        cache.put(serviceConfig.getId(), serviceConfig);

        logger.info(":: ADDING " + serviceConfig.toString() + " ::");

        return Response.ok(serviceConfig).build();
    }

    @PUT
    @Path("{id}")
    public Response heartbeat(@PathParam("id") String id) {
        if (!cache.containsKey(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        cache.submitToKey(id, new EntryProcessor<String, ServiceConfig>() {
            @Override
            public Object process(Map.Entry<String, ServiceConfig> entry) {
                ServiceConfig valueMap = entry.getValue();
                valueMap.setLastCheck(System.currentTimeMillis());
                entry.setValue(valueMap);
                return null;
            }

            @Override
            public EntryBackupProcessor getBackupProcessor() {
                return null;
            }
        });
        return Response.ok(mapped()).build();
    }

    private Map<String, Collection<ServiceConfig>> mapped() {
        Map<String, Collection<ServiceConfig>> computed = new HashMap<>();

        cache.forEach((s, service) -> {
            if (System.currentTimeMillis() - service.getLastCheck() > leaseTime) {
                logger.info(":: REMOVING " + service.toString() + "... R.I.P. ::");
                cache.remove(service.getId());
            } else {
                String type = service.getName();
                if (!computed.containsKey(type)) {
                    computed.put(type, new ArrayList<>());
                }
                computed.get(type).add(service);
            }
        });
        return computed;
    }

    private String simpleJsonMessage(String message) {
        return "{\"message\": \"" + message + "\"}";
    }
}
