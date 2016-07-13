## Pre requisites
Must run on any Java EE 7 application server, and JDK 8.
Server can also run on web servers (Tomcat) with CDI, JAXRS and Websockets 
not tested though (just use an application server !)

## Running on Docker
Build using the provided Dockerfile

    docker build -t registry .
    docker run -it -d -p 8080:8080 registry reg
    
## How it works
Simply a websocket endpoint backed by a concurrent map (no distributed cache yet).
Also contains a simple REST endpoint to provide connected nodes:

    http://localhost:8080/api/services
    
    [
      {
        "name": "balance",
        "links": [],
        "instances": [
          {
            "id": "1",
            "address": "http://localhost:8080/balance/rest",
            "since": "2016-07-14 12:10:14",
            "available": true
          }
        ]
      },
      {
        "name": "account",
        "links": [],
        "instances": [
          {
            "id": "0",
            "address": "http://localhost:8080/account/rest",
            "since": "2016-07-14 12:10:12",
            "available": true
          }
        ]
      }
    ]
    
