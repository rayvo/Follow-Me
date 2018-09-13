package com.cewit.fm1.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 7. 18.
 * qvo@cs.stonybrook.edu
 */

public class Tour implements Serializable {
    private String id;
    private String name;
    private String info;
    private int totalTime;
    private int totalCost;
    private int totalDistance;
    private HashMap<String,List<String>> days;
    private HashMap<String,List<Integer>> times;
    private String startTime;
    private List<String> imageIds;
    //private List<String> cityIds; // TODO Will be reused later

    private String cityId;

    public Tour() {

    }

    public Tour(String id, String name, String info, int totalTime, int totalCost, int totalDistance, HashMap<String, List<String>> days, HashMap<String, List<Integer>> times, String startTime, List<String> imageIds, String cityId) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.totalTime = totalTime;
        this.totalCost = totalCost;
        this.totalDistance = totalDistance;
        this.days = days;
        this.times = times;
        this.startTime = startTime;
        this.imageIds = imageIds;
        this.cityId = cityId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public HashMap<String, List<String>> getDays() {
        return days;
    }

    public HashMap<String, List<Integer>> getTimes() {
        return times;
    }

    public void setTimes(HashMap<String, List<Integer>> times) {
        this.times = times;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<String> getImageIds() {
        return imageIds;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void setDays(HashMap<String, List<String>> days) {
        this.days = days;
    }

    public void setImageIds(List<String> imageIds) {
        this.imageIds = imageIds;
    }
}

