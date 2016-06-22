package com.josue.micro.service.registry.rest;


import com.josue.micro.service.registry.ServiceControl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;

/**
 * Created by Josue on 09/06/2016.
 */
@Path("services")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServiceResource implements Serializable {

    @Inject
    private ServiceControl control;

    @GET
    public Response getServices(@QueryParam("name") String serviceName) {
        return Response.ok(control.getServices(serviceName)).build();
    }

}
