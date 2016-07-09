package com.josue.micro.service.registry.link;


import com.josue.micro.service.registry.service.ServiceConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by Josue on 09/06/2016.
 */
@Path("links")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LinkService implements Serializable {

    @Inject
    private LinkControl linkControl;

    @GET
    public Response getServices(@QueryParam("name") String serviceName) {
        return Response.ok(linkControl.getNodes(serviceName)).build();
    }

    @POST
    @Path("{name}")
    public Response addLink(@PathParam("name") String name, ServiceConfig dependency) {
        Set<Node> nodes = linkControl.addLink(name, dependency.getName());
        return Response.status(Response.Status.CREATED).entity(nodes).build();
    }

    @PUT
    @Path("{name}")
    public Response disconnectNode(@PathParam("name") String name) {
        return Response.ok(linkControl.disconnectNode(name)).build();
    }

    @DELETE
    @Path("{name}")
    public Response removeNode(@PathParam("name") String name) {
        linkControl.removeNode(name);
        return Response.noContent().build();
    }

}
