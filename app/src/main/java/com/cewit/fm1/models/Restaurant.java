package com.cewit.fm1.models;

import java.util.List;

public class Restaurant extends Place {
    //Testing commit and pushes through VCS by Hawon Park

    public Restaurant(){

    }
    public Restaurant(String id, String name, String address, String cityId, String contact, String site, String info, String email, long lat, long lng, int rate, String openTime, String closeTime, int entranceFee, List<Integer> imageIds, String type,  boolean isFavorite, String resType) {
        super(id, name, address, cityId, contact, site, info, email, lat, lng, rate, openTime, closeTime, entranceFee, type, isFavorite, resType);
        setResType(resType);
    }


    public String getType(){
        return super.getType();
    }

    public void setType(String s){
        super.setType(s);
    }
    public String toString(){
        return super.toString();
    }
}
