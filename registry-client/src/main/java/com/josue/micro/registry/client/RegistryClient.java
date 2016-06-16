package com.josue.micro.registry.client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
public class RegistryClient {

    private static final Logger logger = Logger.getLogger(RegistryClient.class.getName());
    private static final String SERVICE_PAT = "services/{id}";

    private final WebTarget serviceRoot;

    public RegistryClient(String registryUrl) {
        serviceRoot = ClientBuilder.newClient()
                .target(registryUrl)
                .path(SERVICE_PAT);
    }

    public ServiceConfig register(String serviceName) throws RegistryException {
        logger.info(":: Registering service ::");

        ServiceConfig service = new ServiceConfig();
        service.setName(serviceName);

        Response response = null;
        try {
            response = serviceRoot
                    .request()
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.json(service));
            if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
                RegistryException registryException = readException(response);
                logger.log(Level.SEVERE, "Error while registring service", registryException);
                throw registryException;
            }

            return response.readEntity(ServiceConfig.class);

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void deregister(String serviceId) {
        logger.info(":: Deregisteing service ::");

        Response response = null;
        try {
            response = serviceRoot
                    .resolveTemplate("id", serviceId)
                    .request()
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .delete();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Could not deregister service, although it will expires soon");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public Map<String, ServiceConfig> getServices(String serviceName) throws RegistryException {
        logger.info(":: Executing heartbeat ::");

        Response response = null;
        try {
            response = serviceRoot
                    //TODO check resolveTemplate will work
                    .queryParam("name", serviceName)
                    .request()
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get();

            if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
                RegistryException registryException = readException(response);
                logger.log(Level.SEVERE, "Error while registring service", registryException);
                throw registryException;
            }

            return response.readEntity(new GenericType<Map<String, ServiceConfig>>() {
            });

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    //easier for client use
    public Map<String, ServiceConfig> getServices() throws RegistryException {
        return getServices(null);
    }

    public boolean heartbeat(String serviceId) throws RegistryException {
        logger.info(":: Executing heartbeat ::");

        Response response = null;
        try {
            response = serviceRoot
                    .resolveTemplate("id", serviceId)
                    .request()
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .put(null);
            return true;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Could not deregister service, although it will expires soon");
            return false;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private RegistryException readException(Response response) {
        ExceptionBean exceptionBean = response.readEntity(ExceptionBean.class);
        return new RegistryException(exceptionBean.getMessage(), exceptionBean.getCode(), response.getStatus());
    }

}
