package com.cewit.fm1.models;

/**
 * Created by Taeyu Im on 18. 7. 18.
 * qvo@cs.stonybrook.edu
 */

public class Transport {
    protected String id;
    protected String name;
    protected int cost;
    protected int distance;
    protected int time;
    protected String type;
    private int period;
    private String firstBus;
    private String lastBus;


    public Transport() {
    }

    public Transport(String id, String name, int cost, int distance, int time, String type, int period, String firstBus, String lastBus) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.distance = distance;
        this.time = time;
        this.type = type;
        this.period = period;
        this.firstBus = firstBus;
        this.lastBus = lastBus;
    }

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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getFirstBus() {
        return firstBus;
    }

    public void setFirstBus(String firstBus) {
        this.firstBus = firstBus;
    }

    public String getLastBus() {
        return lastBus;
    }

    public void setLastBus(String lastBus) {
        this.lastBus = lastBus;
    }

}
