package com.cewit.fm1.models;

import java.util.List;

public class Restaurant extends Place {

    private String resType;

    //Testing commit and pushes through VCS by Hawon Park


    //description is left blank
    //entranceFee is default 0
    //ratingStar is restaurant rating
    // placeImageId is sourced from drawable
    //requiredTime is 60minutes (one hour to eat)
    //stops was left null out of confusion
    public Restaurant(String id, String name, String address, String cityId, String contact, String site, String info, String email, long lat, long lng, int rate, String openTime, String closeTime, int entranceFee, List<Integer> imageIds, String type,  boolean isFavorite, String accType) {
        super(id, name, address, cityId, contact, site, info, email, lat, lng, rate, openTime, closeTime, entranceFee, imageIds, type, isFavorite);
        this.resType = accType;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }


}
