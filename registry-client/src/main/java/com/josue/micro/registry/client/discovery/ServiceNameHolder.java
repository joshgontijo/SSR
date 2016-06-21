package com.josue.micro.registry.client.discovery;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josue on 21/06/2016.
 */
public class ServiceNameHolder {

    private static final Logger logger = Logger.getLogger(ServiceNameHolder.class.getName());

    private static String serviceName;

    public static void setServiceName(String name) {
        if (serviceName != null && !serviceName.isEmpty()) {
            logger.log(Level.WARNING, ":: Multiple services found: [\'{0}\',\'{1}\'], feature not supported yet ::",
                    new Object[]{serviceName, serviceName});
        }
        serviceName = name;
    }

    public static String getServiceName() {
        return serviceName;
    }
}
