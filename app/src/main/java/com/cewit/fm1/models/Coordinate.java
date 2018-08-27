package com.cewit.fm1.models;

public class Coordinate {

    private double latitude;
    private double longitude;

    public Coordinate(double la, double lo){
        this.latitude = la;
        this.longitude = lo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
