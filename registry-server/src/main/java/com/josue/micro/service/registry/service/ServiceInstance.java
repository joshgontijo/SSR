package com.josue.micro.service.registry.service;

import java.util.Date;

/**
 * Created by Josue on 12/07/2016.
 */
public class ServiceInstance {

    private String id;
    private String address;
    private Date since;
    private Date downSince;
    private boolean available = true;

    public ServiceInstance() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Date getDownSince() {
        return downSince;
    }

    public void setDownSince(Date downSince) {
        this.downSince = downSince;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceInstance)) return false;

        ServiceInstance that = (ServiceInstance) o;

        return address != null ? address.equals(that.address) : that.address == null;

    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }
}
