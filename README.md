#S.S.R. (Simple Service Registry)

##What's this
A simplistic key-value that store that allows services
to register its endpoints for discovery.

##How it works ?
It's based on websockets server and client that connects to each other
in order to stabilish who's connected.

##Note
This is just a sample project to test some concepts, it's not replicated at the moment,
which means that it would be a single point of failure on your system. Use for testing only.

## Basic diagram
 - Service A connects to the registry and stablish a websocket connection
 - Service B connects to the registry and stablish a websocket connection
 - Registry server push Service B metadata to Service A
 - Registry server push Service A metadata to Service B
 - Services communicates with each other

![Overview](overview.png)

## More details
Check inside each project for more details on how to run.