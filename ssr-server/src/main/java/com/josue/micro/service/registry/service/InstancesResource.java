package com.josue.micro.service.registry.service;

import com.josue.micro.service.registry.ServiceException;
import com.josue.micro.service.registry.ws.SessionStore;
import com.josue.ssr.common.Instance;

import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Created by Josue on 23/08/2016.
 */
@Path("instances")
public class InstancesResource {

    @Inject
    private ServiceControl control;

    @Inject
    private SessionStore sessionStore;

    @PUT
    @Path("{instanceId}")
    public Response updateServiceState(@PathParam("instanceId") String instanceId, Instance instance) throws Exception {
        if (instance == null || instance.getState() == null) { //it only supports state update as for now
            throw new ServiceException(400, "Invalid instance");
        }

        Instance updated = control.updateInstanceState(instanceId, instance.getState());
        sessionStore.pushInstanceState(updated);

        return Response.noContent().build();
    }
}
