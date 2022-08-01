package com.sc.lib_weather.common;

/**
 * 需要根据编号或者中文返回图片id 也就是另一个名称
 * @author sc
 * @date 2021/9/26 15:32
 */
public class WeatherCodeUtil {

    public static final String[] XIAOMI_INDEXS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
            , "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"
            , "20", "21", "22", "23", "24", "25", "26", "27", "28", "29"
            , "30", "31", "32", "33", "34", "35", "53", "99"
    };

    public static final String[] XIAOMI_NAMES = {
            "晴", "多云", "阴", "阵雨", "雷阵雨"
            , "雷阵雨并伴有冰雹" , "雨夹雪", "小雨", "中雨", "大雨"
            , "暴雨", "大暴雨", "特大暴雨", "阵雪", "小雪"
            , "中雪", "大雪", "暴雪", "雾", "冻雨"
            , "沙尘暴", "小雨-中雨", "中雨-大雨", "大雨-暴雨", "暴雨-大暴雨"
            , "大暴雨-特大暴雨", "小雪-中雪", "中雪-大雪", "大雪-暴雪", "浮尘"
            , "扬沙", "强沙尘暴", "飑", "龙卷风", "若高吹雪", "轻雾", "霾", "未知"
    };

    // 0 晴天 sun
    // 1 云 阴天 cloud
    // 3 雨 rain
    // 4 雨雪
    // 5 雪 snow
    // 6 沙尘暴 sand
    // 7 龙卷风 wind
    // 8 雾霾 fog
    // 9 未知
    public static final String[] WEATHER_IMGS = {
            "sun", "cloud", "cloud", "rain", "rain"
            , "rain", "snow", "rain", "rain", "rain"
            , "rain", "rain", "rain", "snow", "snow"
            , "snow", "snow", "snow", "fog", "snow"
            , "sand", "rain", "rain", "rain", "rain"
            , "rain", "snow", "snow", "snow", "sand"
            , "sand", "sand", "wind", "wind", "wind", "fog", "fog", "unknow"
    };


}
