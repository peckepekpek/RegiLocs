package com.pekapps.regilocs.entity;

import com.pekapps.regilocs.entity.Location;

import java.util.List;

/**
 * Created by farnauvi on 13/12/2017.
 */

public class User {

    private long id;
    private String name;
    private String role;
    private List<Device> devices;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
