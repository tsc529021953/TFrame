package com.sc.xwservice.xp.bean;

public class XPBean {
    public int year;

    public int month;

    public int day;

    public int hour;

    public int minutes;

    public int second;

    public String week;

    public String tem;

    public String location;

    public String weather;

    public int weatherIndex;
    public XPBean(){}
    public void copy(XPBean xpBean) {
        this.year = xpBean.year;
        this.month = xpBean.month;
        this.day = xpBean.day;
        this.hour = xpBean.hour;
        this.minutes =xpBean. minutes;
        this.second = xpBean.second;
        this.week = xpBean.week;
        this.tem =xpBean. tem;
        this.location = xpBean.location;
        this.weather = xpBean.weather;
        this.weatherIndex =xpBean. weatherIndex;
    }

    public enum Weather{
        SUN,
    }

    @Override
    public String toString() {
        return "XPBean{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minutes=" + minutes +
                ", second=" + second +
                ", week='" + week + '\'' +
                ", tem='" + tem + '\'' +
                ", location='" + location + '\'' +
                ", weather='" + weather + '\'' +
                ", weatherIndex=" + weatherIndex +
                '}';
    }
}
