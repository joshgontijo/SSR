## Pre requisites
Must run on any Java EE 7 application server, and JDK 7.
Server can also run on web servers (Tomcat) with CDI, JAXRS and JSR-356 Websockets client
not tested though (just use an application server !)

## Setup

#### Maven dependency
- Download the source and build by yourself =).
- Add as maven dependency

        <dependency>
           <groupId>com.josue.micro</groupId>
           <artifactId>ssr-client-jee</artifactId>
           <version>0.4</version>
        </dependency>

#### JAXRS application

    @EnableClient
    @EnableDiscovery(name = "myApp")
    @ApplicationPath("api")
    public class JaxrsApp extends Application {
    }

On this code:
 - `@EnableClient` flag that the client will listen to other services state changes. The server will push data to this app.
 - `@EnableDiscovery` make the app available for discovery under name spcified on `name` parameter.

Which one to choose ?
Given three services, which rest invocation is the following `A -> B -> C`.
 - `Service A` will have only `@EnableClient`, since it only connects to service B, and doesn't need to be discoverable.
 - `Service B` is a client of `Service A` and consumer of `Service C` so it should have `@EnableClient` and `@EnableDiscovery`
 - `Service C` doesn't require to call any other service, but needs to be discovered by `Service B`, so it should have `@EnableDiscovery`.


## Configuration
In order to connect to the registry server, the client needs to specify on which host and port it's running, also,
where the registry server is. The three basic methods of passing these informations are:
 - Properties file
 - System property
 - Environment variables

These options follow order of preference by overring each other.

Clients will use the `localhost` address and port `8080` as default values for discovery.
To change the configuration you will have multiple option

#### Properties file (registry.properties)
`registry.host` and `registry.port` is where the service registry is.

`service.host` and `service.port` is where your application is

For example:

    registry.host=REGISTRY-HOST
    registry.port=REGISTRY-PORT
    service.host=SERVICE-HOST
    service.port=SERVICE-PORT

#### System properties

    java -jar myapp.jar -Dregistry.host=HOST -Dregistry.port=PORT ...

#### Environment variables
When your application starts it will pick these keys from the environment and override any other value set on system properties or properties file.
In order to be easier to run with bash scripts, the properties keys in this approach change a little bit, as follows.

    REGISTRY_HOST
    REGISTRY_PORT
    SERVICE_HOST
    SERVICE_PORT


## Running with Docker
 - The best way to run with docker is setting environment variables to pass these values to the application.
 - Because docker uses a bridge adaptor by default, application running inside a container will not be able to see the registry server
 nor other clients if you don't set a `link` or add a `network` or run the container in host mode.
 - SSR has its limitations but leave the responsability to you to decide what's the best approach for your network infrastructure.


##Properties file with multi environments
Client application can also create multiple files for each environment and switch between them by using the `ssr.environment` variable.
For example:

    registry.properties
    registry-test.properties
    registry-prod.properties

If specified, the environment variable or system property will picked up and used to define which file to use. If no value is specified,
default will be used. For example

    //registry-test.properties
    java -jar myapp.jar -Dssr.environment=test

    //registry.properties
    java -jar myapp.jar

Environment variables can also be used, by simply setting `SSR_ENVIRONMENT`


## Acessing services
To access the service URL simply use:

    @Inject
    ServiceStore serviceStore;
      
    ServiceInstance any = serviceStore.get("serviceName"); //default roundRobin
    ServiceInstance random = serviceStore.get("serviceName", Strategy.random());
    ServiceInstance roundRobin = serviceStore.get("serviceName", Strategy.roundRobin());
    
The `Strategy` class provides a way of customising the load balancing of the requests.