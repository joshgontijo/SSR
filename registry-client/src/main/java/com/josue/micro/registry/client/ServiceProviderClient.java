package com.josue.micro.registry.client;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
public class ServiceProviderClient extends Client {

    private static final Logger logger = Logger.getLogger(ServiceProviderClient.class.getName());

    private static final String SERVICE_URL = "service.url";
    private final String serviceUrl;

    public ServiceProviderClient() {
        serviceUrl = System.getProperty(SERVICE_URL);
        if (registryUrl == null || registryUrl.isEmpty()) {
            throw new IllegalStateException(":: Could not find value for system property '" + SERVICE_URL + "' ::");
        }
    }

    //TODO implement retry in case provider is down
    public ServiceConfig register(String serviceName) throws RegistryException {
        logger.info(":: Registering service ::");

        ServiceConfig config = new ServiceConfig();
        config.setAddress(serviceUrl);
        config.setName(serviceName);

        Response response = null;
        try {
            response = serviceRoot
                    .resolveTemplate("id", "")
                    .request()
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.json(config));
            if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
                RegistryException registryException = readException(response);
                logger.log(Level.SEVERE, ":: Error while registring service ::", registryException);
                throw registryException;
            }

            return response.readEntity(ServiceConfig.class);

        } catch (Exception ex) {
            logger.log(Level.SEVERE, ":: Could register service '" + serviceName + "' ::", ex);
            RegistryException registryException = readException(response);
            throw registryException;
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
            logger.log(Level.SEVERE, "Could not deregister service, although it will expires soon", ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void heartbeat(String serviceId) throws RegistryException {
        logger.info(":: Executing heartbeat ::");

        Response response = null;
        try {
            response = serviceRoot
                    .resolveTemplate("id", serviceId)
                    .request()
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .put(null);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Could not send heartbeat");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

}
