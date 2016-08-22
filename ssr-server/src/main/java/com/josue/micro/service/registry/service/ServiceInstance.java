package com.josue.micro.service.registry.service;


import java.util.Date;

/**
 * Created by Josue on 12/07/2016.
 */
public class ServiceInstance {

    public enum State {
        UP, DOWN, OUT_OF_SERVICE
    }

    private String id;
    private String address;
    private Date since;
    private Date downSince;
    private String serviceName;
    private State state = State.DOWN;

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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getDownSince() {
        return downSince;
    }

    public void setDownSince(Date downSince) {
        this.downSince = downSince;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void updateInstanceState(ServiceInstance.State newState) {
        state = newState;
        if (ServiceInstance.State.DOWN.equals(newState)) {
            downSince = new Date();
        }
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

    @Override
    public String toString() {
        return "ServiceInstance{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", since=" + since +
                ", downSince=" + downSince +
                ", state=" + state +
                '}';
    }
}
