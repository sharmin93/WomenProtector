package com.oweshie.womenprotector.womenprotector.common;

public class DataBaseData {
    private Long date;
    private String name;
    private String latitude;
    private String longitude;


    public DataBaseData() {
    }

    public Long getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
