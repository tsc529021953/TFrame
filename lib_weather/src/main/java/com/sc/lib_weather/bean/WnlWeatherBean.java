package com.sc.lib_weather.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.List;

/**
 * @author sc
 * @date 2021/9/26 18:12
 */

@XStreamAlias("resp")
public class WnlWeatherBean {

    @XStreamAsAttribute
    public String city;

    @XStreamAsAttribute
    public String updatetime;

    @XStreamAsAttribute
    public String wendu;

    public String shidu;

    public List<Weather> forecast;

    @XStreamAlias("weather")
    public class Weather{
        public String date;

        public String high;

        public String low;

        public Day day;
//
        public Night night;
    }


    @XStreamAlias("day")
    public class Day{
        public String type;

        public String fengxiang;

        public String fengli;
    }
    @XStreamAlias("night")
    public class Night{
        public String type;

        public String fengxiang;

        public String fengli;
    }


}
