package com.josue.micro.service.registry.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Josue on 09/06/2016.
 */
public class ServiceConfig implements Serializable {

    private String id;
    private String name;
    private String address;
    private Date since;
    private Set<ServiceConfig> dependencies = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public Set<ServiceConfig> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<ServiceConfig> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", address='" + address + "\'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceConfig)) return false;

        ServiceConfig that = (ServiceConfig) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (since != null ? !since.equals(that.since) : that.since != null) return false;
        return dependencies != null ? dependencies.equals(that.dependencies) : that.dependencies == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (since != null ? since.hashCode() : 0);
        result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
        return result;
    }
}

