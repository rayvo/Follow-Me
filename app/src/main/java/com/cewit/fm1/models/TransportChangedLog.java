package com.cewit.fm1.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 9. 19.
 * qvo@cs.stonybrook.edu
 */

public class TransportChangedLog implements Serializable {
    List<String> placeIds;
    List<String> transportTypes;

    public TransportChangedLog() {
        placeIds = new ArrayList<String>();
        transportTypes = new ArrayList<String>();
    }

    public void addData (String strPlaceId, String strTransportType) {
        placeIds.add(strPlaceId);
        transportTypes.add(strTransportType);
    }

    public TransportChangedLog(List<String> placeIds, List<String> transportTypes) {
        this.placeIds = placeIds;
        this.transportTypes = transportTypes;
    }

    public List<String> getPlaceIds() {
        return placeIds;
    }

    public void setPlaceIds(List<String> placeIds) {
        this.placeIds = placeIds;
    }

    public List<String> getTransportTypes() {
        return transportTypes;
    }

    public void setTransportTypes(List<String> transportTypes) {
        this.transportTypes = transportTypes;
    }
}
