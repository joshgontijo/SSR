package com.josue.micro.service.registry;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class handle all exceptions for the app, should be added to the resources in ApplicationConfig class
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class RestExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger logger = Logger.getLogger(RestExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception exception) {

        String code = UUID.randomUUID().toString().substring(0, 8);

        ExceptionBean bean = new ExceptionBean(code, exception.getMessage());
        int responseStatus = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        if (exception instanceof WebApplicationException) {
            WebApplicationException waex = (WebApplicationException) exception;
            responseStatus = waex.getResponse().getStatus();

        } else if (exception instanceof ServiceException) { // internal server error
            ServiceException ex = (ServiceException) exception;
            responseStatus = ex.getCode();

        } else {//internal server error
            logger.log(Level.SEVERE, "(" + code + ") " + exception.getMessage(), exception);
        }

        return Response.status(responseStatus).type(MediaType.APPLICATION_JSON_TYPE).entity(bean).build();
    }

}
