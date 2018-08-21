package com.cewit.fm1.models;

/**
 * Created by Taeyu Im on 18. 7. 18.
 * qvo@cs.stonybrook.edu
 */

public class City {
    private String id;
    private String countryId;
    private String name;
    private String lat;
    private String lng;

    public City() {
    }

    public City(String id, String countryId, String name, String lat, String lng) {
        this.id = id;
        this.countryId = countryId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public String getCountryId() {
        return countryId;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
