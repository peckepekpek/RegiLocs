package com.pekapps.regilocs.entity;

import java.util.Date;

/**
 * Created by farnauvi on 13/12/2017.
 */

public class Location {
    private long id;
    private Date date;
    private String longitude;
    private String latitude;
    private String altitude;

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }
}
