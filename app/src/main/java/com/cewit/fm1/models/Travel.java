package com.cewit.fm1.models;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 7. 18.
 * qvo@cs.stonybrook.edu
 */

public class Travel {
    private String id;
    private int distance;
    private String from;
    private String to;
    private HashMap<String, Transport> transports;

    public Travel() {
    }

    public Travel(String id, int distance, String from, String to, HashMap<String, Transport> transports) {
        this.id = id;
        this.distance = distance;
        this.from = from;
        this.to = to;
        this.transports = transports;
    }

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public HashMap<String, Transport> getTransports() {
        return transports;
    }

}
