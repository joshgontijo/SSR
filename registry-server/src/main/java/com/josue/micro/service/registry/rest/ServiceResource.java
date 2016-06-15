package com.josue.micro.service.registry.rest;


import com.josue.micro.service.registry.ServiceConfig;
import com.josue.micro.service.registry.ServiceControl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    public Response getServices() {
        return Response.ok(control.getServices()).build();
    }

    @GET
    @Path("{service}")
    public Response getService(@PathParam("service") String service) throws Exception {
        return Response.ok(control.getServiceForType(service)).build();
    }

    @POST
    public Response addService(ServiceConfig serviceConfig) throws Exception {
        return Response.status(Response.Status.CREATED).entity(control.register(serviceConfig)).build();
    }

    @PUT
    @Path("{id}")
    public Response heartbeat(@PathParam("id") String id) throws Exception {
        return Response.ok(control.heartbeat(id)).build();
    }

}
