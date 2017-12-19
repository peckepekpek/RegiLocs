package com.pekapps.regilocs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by farnauvi on 15/12/2017.
 */

public class Device {

    public Device(String id) {
        this.id = id;
    }

    public Device() {
    }

    private String id;
    @JsonIgnore
    private User user;
    private List<Location> locations;

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<Location> getLocations() {
        return locations;
    }




    public void setId(String id) {

        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
