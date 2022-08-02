package com.sc.lib_weather.bean;

import android.location.Address;
import android.location.Location;

/**
 * @author sc
 * @date 2021/9/28 7:59
 */
public class LocationDataBean {

    private static final String CHINA = "中国";

    public Location location;

    public Address address;

//    public LocationBean locationBean;

    public double latitude;

    public double getLatitude() {
        return latitude;
    }

    public double longitude;

    public double getLongitude() {
        return longitude;
    }

    public String country = CHINA;

    /**
     * 省份
     */
    public String admin;

    /**
     * 市级
     */
    public String subAdmin;

    /*区级*/
    public String locality;

    public LocationDataBean(String country, String admin, String subAdmin, String locality, double latitude, double longitude){
        this.country = country;
        this.admin = admin;
        this.subAdmin = subAdmin;
        this.locality = locality;
        this.latitude = latitude;
        this.longitude =longitude;
    }

    public LocationDataBean(String admin, String subAdmin, String locality){
        this(CHINA, admin, subAdmin, locality, 0, 0);
    }

    public LocationDataBean(String admin, String subAdmin, String locality, double latitude, double longitude){
       this(CHINA, admin, subAdmin, locality, latitude, longitude);
    }

    public LocationDataBean(Location location, Address address) {
        this.location = location;
        this.address = address;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.country = address.getCountryName();
        this.admin = address.getAdminArea();
        this.subAdmin = address.getSubAdminArea();
        this.locality = address.getLocality();
    }
}
