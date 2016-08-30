package com.josue.micro.service.registry.service;


import com.josue.micro.service.registry.Service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.Set;

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
    public Response getServices() throws Exception {
        Set<Service> services = control.getServices();
        return Response.ok(services).build();
    }

    @GET
    @Path("{name}")
    public Response getServices(@PathParam("name") String serviceName) throws Exception {
        Service service = control.getService(serviceName);
        return Response.ok(service).build();
    }


//    @PUT
//    @Path("{name}")
//    public Response deleteUnavailableNodes(@PathParam("name") String name) throws Exception {
//        control.deleteUnavailableNodes(name);
//        return Response.ok().build();
//    }


}
