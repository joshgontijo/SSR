## Pre requisites
Must run on any Java EE 7 application server, and JDK 8.
Server can also run on web servers (Tomcat) with CDI, JAXRS and Websockets 
not tested though (just use an application server !)

## Running on Docker
Build using the Dockerfile provided

    docker build -t registry .
    docker run -it -d -p 8080:8080 registry reg
    
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
    
