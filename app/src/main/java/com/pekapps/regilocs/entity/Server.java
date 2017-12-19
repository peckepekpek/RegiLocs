package com.pekapps.regilocs.entity;

/**
 * Created by farnauvi on 15/12/2017.
 */

public class Server {

    private String status;
    private String ip;
    private String port;

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getStatus() {
        return status;
    }

    public Server(String ip, String port, String status) {
        this.ip = ip;
        this.port = port;
        this.status = status;
    }

    public Server(String status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
