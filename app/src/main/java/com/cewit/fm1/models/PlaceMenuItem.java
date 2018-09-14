package com.cewit.fm1.models;

/**
 * Created by Taeyu Im on 18. 9. 14.
 * qvo@cs.stonybrook.edu
 */

public class PlaceMenuItem {
    private String id;
    private String title;
    private String address;
    private String number;
    private String type;

    public PlaceMenuItem() {
    }


    public PlaceMenuItem(String title) {
        this.title = title;
    }

    public PlaceMenuItem(String mId, String mTitle) {
        this.title = title;
    }

    public PlaceMenuItem(String title, String imageRes, String address, String number, String type) {
        this.title = title;
        this.address = address;
        this.number = number;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
