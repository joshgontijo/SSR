package com.josue.micro.registry;

import com.josue.micro.registry.service.JaxrsApp;
import com.josue.micro.registry.ws.ServiceEndpoint;
import com.josue.microserver.core.Microserver;
import com.josue.microserver.core.MicroserverConfig;
import com.josue.microserver.core.config.CoreConfig;
import com.josue.microserver.core.config.JaxrsConfig;
import com.josue.microserver.core.config.WebsocketConfig;
import com.josue.ssr.common.EndpointPath;


/**
 * Created by Josue on 21/08/2016.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        MicroserverConfig config = new MicroserverConfig()
                .jaxrs(new JaxrsConfig("/api", JaxrsApp.class))
                .websocket(new WebsocketConfig(EndpointPath.WS_BASE_PATH).addEndpoint(ServiceEndpoint.class))
                .core(new CoreConfig().port(9090));


        new Microserver(config).start();
    }
}
