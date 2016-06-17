package com.josue.micro.registry.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Josue on 16/06/2016.
 */
public class ServicePropertiesLoader {

    private static final String SERVICE_PROPERTIES = "service.properties";

    public static final Properties load()  {
        Properties props = new Properties();

        InputStream is = ServicePropertiesLoader.class.getClassLoader().getResourceAsStream(SERVICE_PROPERTIES);
        if(is == null){

        }

        try {
            props.load(ServicePropertiesLoader.class.getClassLoader().getResourceAsStream(SERVICE_PROPERTIES));
        } catch (IOException e) {
            throw new RuntimeException(":: Could not load '" + SERVICE_PROPERTIES + "'::");
        }

        return props;
    }

}
