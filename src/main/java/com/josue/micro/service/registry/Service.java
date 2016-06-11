package com.josue.micro.service.registry;

import java.io.Serializable;

/**
 * Created by Josue on 09/06/2016.
 */
public class Service implements Serializable {

    private String id;
    private String url;
    private String name;
    private long lastCheck;

    public long getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
    }

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

        Service service = (Service) o;

        if (lastCheck != service.lastCheck) return false;
        if (id != null ? !id.equals(service.id) : service.id != null) return false;
        if (url != null ? !url.equals(service.url) : service.url != null) return false;
        return this.name != null ? this.name.equals(service.name) : service.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (lastCheck ^ (lastCheck >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", lastCheck=" + lastCheck;
    }
}
