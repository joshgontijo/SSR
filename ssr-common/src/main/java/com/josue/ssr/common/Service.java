package com.josue.ssr.common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Josue on 09/06/2016.
 */
public class Service implements Serializable {

    private String name;
    private final Set<Instance> instances = new HashSet<>();
    private final Set<String> links = new HashSet<>();

    public Service() {
    }

    public Service(String name) {
        this.name = name;
    }

    public Set<String> getLinks() {
        return links;
    }

    public Set<Instance> getInstances() {
        return new HashSet<>(instances);
    }

    public boolean containsInstance(String instanceId) {
        for (Instance instance : instances) {
            if (instance.getId().equals(instanceId)) {
                return true;
            }
        }
        return false;
    }

    public Instance addInstance(Instance newInstance) {
        Instance existent = null;
        for (Instance instance : instances) {
            if (instance.equals(instance)) {
                existent = instance;
            }
        }

        //already exists and is not UP, remove it
        if (existent != null && !Instance.State.UP.equals(existent.getState())) {
            instances.remove(existent.getId());
        }
        instances.add(newInstance);
        return newInstance;
    }

    public synchronized void removeInstance(String instanceId) {
        Iterator<Instance> it = instances.iterator();
        while (it.hasNext()) {
            if (it.next().getId().equals(instanceId)) {
                it.remove();
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;

        Service that = (Service) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

