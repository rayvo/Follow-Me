package com.cewit.fm1.models;

import java.util.List;

public class Restaurant extends Place {
    //Testing commit and pushes through VCS by Hawon Park

    public Restaurant(){

    }
    public Restaurant(String id, String name, String address, String cityId, String contact, String site, String info, String email, long lat, long lng, int rate, String openTime, String closeTime, int entranceFee, List<Integer> imageIds, String type,  boolean isFavorite, String resType) {
        super(id, name, address, cityId, contact, site, info, email, lat, lng, rate, openTime, closeTime, entranceFee, imageIds, type, isFavorite);
        setResType(resType);
    }

    public Restaurant(String id, String name, String address, String cityId, String contact, String site, String info, String email, long lat, long lng, int rate, String openTime, String closeTime, int entranceFee, List<Integer> imageIds, String type,  boolean isFavorite) {
        super(id, name, address, cityId, contact, site, info, email, lat, lng, rate, openTime, closeTime, entranceFee, imageIds, type, isFavorite);
    }

    public String toString(){
        return super.toString();
    }
}
