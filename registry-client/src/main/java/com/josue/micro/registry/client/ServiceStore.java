package com.josue.micro.registry.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Josue on 19/06/2016.
 */
public class ServiceStore extends ConcurrentHashMap<String, List<ServiceConfig>> {

    public ServiceConfig getAny(String serviceName){
        if(!this.containsKey(serviceName)){
            return null;
        }
        return this.get(serviceName).get(0);
    }

    //TODO implement get strategy(round robin) etc
    public ServiceConfig getAny(String serviceName, String strategy){
        throw new RuntimeException(":: Not implemented yet ::");
    }

    public void addService(String key, ServiceConfig value) {
        if(!containsKey(key)){
            this.put(key, new ArrayList<ServiceConfig>());
        }
        super.get(key).add(value);
    }

    public void removeService(String id){
       this.values().forEach(c -> c.removeIf(cfg -> cfg.getId().equals(id)));
    }
}
