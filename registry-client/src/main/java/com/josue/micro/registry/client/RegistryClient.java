package com.josue.micro.registry.client;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
public class RegistryClient extends Client {

    private static final Logger logger = Logger.getLogger(RegistryClient.class.getName());


    public Map<String, ServiceConfig> getServices(String serviceName) throws RegistryException {
        logger.info(":: Executing heartbeat ::");

        Response response = null;
        try {
            response = serviceRoot
                    .resolveTemplate("id", "")
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

}
