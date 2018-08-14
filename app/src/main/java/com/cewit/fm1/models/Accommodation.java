package com.cewit.fm1.models;

import java.util.List;

/**
 * Created by Taeyu Im on 18. 8. 14.
 * qvo@cs.stonybrook.edu
 */

public class Accommodation extends Place {
    private String accType;

    public Accommodation() {
    }

    public Accommodation(String id, String name, String address, String cityId, String contact, String site, String info, String email, long lat, long lng, int rate, String openTime, String closeTime, int entranceFee, List<Integer> imageIds, String type,  boolean isFavorite, String accType) {
        super(id, name, address, cityId, contact, site, info, email, lat, lng, rate, openTime, closeTime, entranceFee, imageIds, type, isFavorite);
        this.accType = accType;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
