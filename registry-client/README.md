## Pre requisites
Must run on any Java EE 7 application server, and JDK 7.
Server can also run on web servers (Tomcat) with CDI, JAXRS and JSR-356 Websockets client
(not tested though, just use an application server !

## Configuration
### Maven dependency
- Download the source and build by yourself =).
- Add as maven dependency

        <dependency>
           <groupId>com.josue.micro</groupId>
           <artifactId>registry-client</artifactId>
           <version>1.0</version>
           </dependency>
        <dependency>
        <dependency>
            <groupId>javax</groupId>
            <artifactId>javaee-api</artifactId>
            <version>7.0</version>
            <scope>provided</scope>
        </dependency>

### JAXRS application

    @EnableDiscovery(name = "balance")
    @ApplicationPath("rest")
    public class JaxrsApp extends Application {
    }

### Setting environment variables
Setting the following environment variables are necessary, otherwise the application won't start.
- `serviceUrl`: The full address of you application
- `registryUrl`: The full address of the registry server

#### Applc

    

    docker run -it -d -p 8080:8080 -e serviceUrl=http://192.168.0.7:8081/account -e registryUrl=http://192.168.0.7:8080 account


## How it works
Simply a websocket endpoint backed by a concurrent map (no distributed cache yet).
Also contains a simple REST endpoint to provide connected nodes:

    http://localhost:8080/api/services
    
    {
    	"balance" :
    	 : {
    		"address" : "http://192.168.0.7:8082/balance/rest"
    		"name" : "balance"
    		"id" : "a"
    		"since" : "20160623053313+0000"
    	}
    
    	"account" :
    	 : {
    		"address" : "http://192.168.0.9:8081/account/rest"
    		"name" : "account"
    		"id" : "9"
    		"since" : "20160623053246+0000"
    	}
    }
    
