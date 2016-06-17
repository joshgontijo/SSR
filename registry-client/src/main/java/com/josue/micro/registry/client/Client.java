package com.josue.micro.registry.client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by Josue on 16/06/2016.
 */
public abstract class Client {

    private static final String SERVICE_PAT = "services/{id}";
    private static final String REGISTRY_URL_KEY = "registry.url";

    protected static final int MAX_CONNECT_TRY = 10;


    protected final WebTarget serviceRoot;

    protected final String registryUrl;

    public Client() {
        registryUrl = System.getProperty(REGISTRY_URL_KEY);

        if(registryUrl == null || registryUrl.isEmpty()){
            throw new IllegalStateException(":: Could not find environment property '" + REGISTRY_URL_KEY + "' ::");
        }

        serviceRoot = ClientBuilder.newClient()
                .target(registryUrl)
                .path(SERVICE_PAT);
    }

    protected RegistryException readException(Response response) {
        ExceptionBean exceptionBean = response.readEntity(ExceptionBean.class);
        return new RegistryException(exceptionBean.getMessage(), exceptionBean.getCode(), response.getStatus());
    }
}
