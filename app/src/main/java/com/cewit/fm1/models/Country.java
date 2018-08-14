package com.cewit.fm1.models;

/**
 * Created by Taeyu Im on 18. 7. 19.
 * qvo@cs.stonybrook.edu
 */

public class Country {
    private String id;
    private String code;
    private String name;

    public Country() {
    }

    public Country(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
