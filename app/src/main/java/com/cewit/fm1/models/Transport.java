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


    public Transport() {
    }

    public Transport(String id, String name, int cost, int distance, int time, String type) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.distance = distance;
        this.time = time;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getDistance() {
        return distance;
    }

    public int getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

}
