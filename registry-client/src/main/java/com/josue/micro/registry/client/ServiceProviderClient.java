package com.josue.micro.registry.client;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 16/06/2016.
 */
public class ServiceProviderClient extends Client {

    private static final Logger logger = Logger.getLogger(ServiceProviderClient.class.getName());
    private static final String SERVICE_URL = "service.url";
    private static final int MAX_RETRY_POLICY = 10;
    private static String serviceId;

    private final ScheduledExecutorService executorService;

    public ServiceProviderClient(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    public void register(String serviceName) {
        executorService.schedule(new Register(serviceName), 0, TimeUnit.SECONDS);
    }

    public void deregister() {
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

    class Register implements Runnable {

        private final String serviceName;
        private final String serviceUrl;

        private int retryCount = 0;

        Register(String serviceName) {
            this.serviceName = serviceName;
            serviceUrl = System.getProperty(SERVICE_URL);
            if (registryUrl == null || registryUrl.isEmpty()) {
                throw new IllegalStateException(":: Could not find value for system property '" + SERVICE_URL + "' ::");
            }
        }

        public void run() {
            logger.log(Level.INFO, ":: Trying to register service \'{0}\' ::", serviceName);

            if (retryCount++ > MAX_RETRY_POLICY) {
                logger.log(Level.WARNING, ":: Max retry policy exceeded ::");
                return;
            }

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
                    //TODO retry policy
                    executorService.schedule(this, 10, TimeUnit.SECONDS);
                }

                ServiceConfig serviceConfig = response.readEntity(ServiceConfig.class);
                logger.log(Level.INFO, ":: Registered executorService \'{0}\', id:{1}, leaseTime:{2} ::",
                        new Object[]{serviceConfig.getName(), serviceConfig.getId(), serviceConfig.getLeaseTime()});

                //assign upper class an ID, so we can deregister later
                serviceId = serviceConfig.getId();

                executorService.schedule(new Heartbeat(serviceConfig), 10, TimeUnit.SECONDS);

            } catch (Exception ex) {
                logger.log(Level.SEVERE, ":: Could register executorService '" + serviceName + "' ::", ex);
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
    }

    class Heartbeat implements Runnable {

        private final ServiceConfig config;

        Heartbeat(ServiceConfig config) {
            this.config = config;
        }

        public void run() {
            logger.info(":: Executing heartbeat ::");
            Response response = null;
            try {
                response = serviceRoot
                        .resolveTemplate("id", config.getId())
                        .request()
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .put(null);

                //server down or heartbeat expired... retry
                if (Response.Status.OK.equals(response.getStatusInfo())) {
                    executorService.schedule(new Register(config.getId()), 10, TimeUnit.SECONDS);
                } else {
                    register(config.getName());
                }

            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Could not send heartbeat");
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
    }


}
