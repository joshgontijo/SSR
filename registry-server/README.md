## Simple service-registry
Distributed key-value storage for service lookup

### Environment parameters
- *service.ttl*: Time to live, default is 20000

### Root path
    /service-registry

#### Retrieve all available services instances for all services types
    GET /services/

#### Retrieve all available services for a given name
    GET /services/{serviceName}

#### Register a new service
    POST /services
    {
      "name": "account",
      "url": "127.0.0.1"
    }

#### Heartbeat
    PUT /services/{id}