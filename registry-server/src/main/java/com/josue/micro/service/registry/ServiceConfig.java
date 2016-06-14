package com.josue.micro.service.registry;

import java.io.Serializable;

/**
 * Created by Josue on 09/06/2016.
 */
public class ServiceConfig implements Serializable {

    private String id;
    private String url;
    private String port;
    private String name;
    private Integer leaseTime;
    private long lastCheck;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(Integer leaseTime) {
        this.leaseTime = leaseTime;
    }

    public long getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceConfig)) return false;

        ServiceConfig that = (ServiceConfig) o;

        if (lastCheck != that.lastCheck) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return leaseTime != null ? leaseTime.equals(that.leaseTime) : that.leaseTime == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (leaseTime != null ? leaseTime.hashCode() : 0);
        result = 31 * result + (int) (lastCheck ^ (lastCheck >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", port='" + port + '\'' +
                ", name='" + name + '\'' +
                ", leaseTime='" + leaseTime + '\'' +
                ", lastCheck=" + lastCheck;

    }
}

