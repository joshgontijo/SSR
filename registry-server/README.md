## Simple service-registry
Distributed key-value storage for service lookup

### Environment parameters
- *service.default.leaseTime*: Time to live (in seconds), default is 60

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
      "url": "127.0.0.1",
      "port": 8081
    }

#### Heartbeat
    PUT /services/{id}