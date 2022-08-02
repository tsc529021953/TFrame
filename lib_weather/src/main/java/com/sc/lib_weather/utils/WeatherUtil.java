
package com.sc.lib_weather.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.webkit.ValueCallback;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.sc.lib_weather.bean.LocationDataBean;
import com.sc.lib_weather.bean.WeatherBean;
import com.sc.lib_weather.bean.WnlWeatherBean;
import com.sc.lib_weather.bean.XiaoMiWeaherBaen;
import com.sc.lib_weather.common.WeatherCodeUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * 天气信息类
 * 1. 判断当前网络状态
 * 2. 获取当前定位
 * 3. 逐个申请信息
 * 4. 将信息传递出去
 * 5. 也可单独提供信息获取
 * 6. https://www.jianshu.com/p/e3e04cf3fc0f
 * 7. 小米天气 https://gitee.com/willrook/api/blob/master/XiaomiWeather.md
 * 10 . http://www.weather.com.cn/data/city3jdata/china.html 城市编号获取
 * 11. https://flash.weather.com.cn/wmaps/xml/ningbo.xml 获取整市相关天气 （中国气象站）
 * 12. http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100 http://wthrcdn.etouch.cn/
 * 12.2 http://wthrcdn.etouch.cn/weather_mini?citykey=101210411 迷你信息
 * 13. https://api.help.bj.cn/apis/weather/?id=101210411 实时天气
 * 14. http://api2.jirengu.com/ 个人api
 * author: sc
 * date: 2021/8/25
 */
public class WeatherUtil {

    private static final String CHINA = "中国", AREAID = "AREAID", WEATHER_CITY = "weather_city.json";

    // 获取实时的信息
    private static final String WEATHER_API = "http://www.weather.com.cn/data/sk/";
    // 获取详细的信息
    private static final String WEATHER_API_ZS = "http://www.weather.com.cn/data/zs/";

    // 小米天气 来自彩云 获取所有数据
    private static final String XIAOMI_WEATHER_API_P1 = "https://weatherapi.market.xiaomi.com/wtr-v3/weather/all?latitude=";
    private static final String XIAOMI_WEATHER_API_P2 = "&longitude=";
    private static final String XIAOMI_WEATHER_API_P3 = "&locationKey=weathercn%3A";
    private static final String XIAOMI_WEATHER_API_P4 = "&days=15&appKey=weather20151024&sign=zUFJoAR2ZVrDy1vF3D07&isGlobal=false&locale=zh_cn";
    // 小米天气 简版
    private static final String XIAOMI_WEATHER_API_SK = "http://weatherapi.market.xiaomi.com/wtr-v2/temp/realtime?cityId=";

    private static final String WNL_WEATHER_API_MINI = "http://wthrcdn.etouch.cn/weather_mini?citykey=";
    private static final String WNL_WEATHER_API = "http://wthrcdn.etouch.cn/WeatherApi?citykey=";

    private static final String LOCATION_ID = "http://www.weather.com.cn/data/city3jdata/china.html";
    private static final String CHINA_CONNTRY_LIST_URL = "https://i.tq121.com.cn/j/wap2016/news/city_data.js?2016";
    private static final String WORLD_COUNTRY_LIST_ID = "https://i.tq121.com.cn/j/wap2016/news/city_ex.js?2016";

    private static final String HTML = ".html";

    Context activity;

    private LocationUtil locationUtil;

    private ValueCallback<WeatherBean> onWeatherChanged;

    private JSONObject cityJsonObject;

    private LocationDataBean location = null;

    public LocationDataBean getLocation() {
        return location;
    }
    //    private WeatherBean weatherBean;


    public WeatherUtil(Context activity, ValueCallback<WeatherBean> onWeatherChanged){
        this.activity = activity;
        this.onWeatherChanged = onWeatherChanged;
        init();
    }

    private void init(){
        String jsonStr = AppJsonFileReader.getJson(activity, WEATHER_CITY);
        if (jsonStr != null) {
            try {
                cityJsonObject = new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        Timber.d(jsonStr);
        locationUtil = new LocationUtil(activity, new ValueCallback<LocationDataBean>() {
            @Override
            public void onReceiveValue(LocationDataBean location) {
                if (location == null)return;
                WeatherUtil.this.location = location;
                Timber.d("定位 " + location.locality);
                // 获取了坐标
                // 判断网络是否连接可用
                if (!NetworkUtil.isNetworkConnected(activity))return;

                // 获取天气信息
                getWeather(location);
            }
        });

//        SmartLocation.with(activity).location().start(new OnLocationUpdatedListener() {
//            @Override
//            public void onLocationUpdated(Location location) {
//                Timber.d("location " + location);
//            }
//        });

    }

    private void getWeather(LocationDataBean location) {
        Timber.d(location.admin + " " + location.subAdmin + " 区 " + location.locality);
        if (!location.country.equals(CHINA)) {
            // 获取国外天气编号
        } else {
            getChinaWeather(location);
        }
    }

    private void getChinaWeather(LocationDataBean bean){
//        String url = CHINA_CONNTRY_LIST_URL;
//        OKHttpUtil.get(url, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Timber.d(e);
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
////                Timber.d(response.body().string());
//                String data = response.body().string();
//                String start = "var city_data=";
//                String end = ";";
//                if (data.startsWith(start)) {
//                    data = data.replace(start, "");
//                }
//                if (data.endsWith(end)) {
//                    data = data.substring(0, data.length() - 2);
//                }
//                if (data != null && !write) {
//                    try {
//                        String admin = removeLast(address.getAdminArea());
//                        if (cityJsonObject.has(admin)) {
//                            String data_sub_admin = cityJsonObject.getString(admin);
//                            String sub_admin = removeLast(address.getSubAdminArea());
//                            JSONObject jo2 = new JSONObject(data_sub_admin);
//                            if (jo2.has(sub_admin)) {
//                                String data_locality = jo2.getString(sub_admin);
//                                String locality = removeLast(address.getLocality());
//                                JSONObject jo3 = new JSONObject(data_locality);
//                                if (jo3.has(locality)){
//                                    String data_info = jo3.getString(locality);
//                                    JSONObject jo4 = new JSONObject(data_info);
//                                    if (jo4.has(AREAID)) {
//                                        Timber.d(jo4.get(AREAID));
//                                    }
//                                }
//                            }
//                        }
//                    } catch (JSONException e) {
//                        Timber.d(data);
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        String id = getChinaCityID(bean.admin, bean.subAdmin, bean.locality);
        Timber.d("id " + id);
        if (id == null)return;
        if (bean.latitude == 0 || bean.longitude == 0) {
            getWeatherByWNL(id, bean, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {

                }
            });
            return;
        }
        getWeatherByXiaomi(id, bean, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String id) {
                getWeatherByWNL(id, bean, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 权限监听
        if (locationUtil != null)locationUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getWeatherByXiaomiSimple(final String id, final ValueCallback<String> callback){
        // 获取实时天气 （小米）
        String url = XIAOMI_WEATHER_API_SK + id;
        OKHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Timber.d("" + e);
                // 换另外一个api
                if (callback != null)
                    callback.onReceiveValue(id);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                XiaoMiWeaherBaen baen = null;
                try {
                    baen = new Gson().fromJson(data, XiaoMiWeaherBaen.class);
                } catch (Exception e) {
                    Timber.d("xiaomi fail " + e);
                    if (callback != null)
                        callback.onReceiveValue(id);
                    return;
                }

                if (baen != null) {
                    WeatherBean weatherBean = new WeatherBean();
                    XiaoMiWeaherBaen.Info1 info = baen.current.feelsLike;
                    weatherBean.temperature = info.value + info.unit;
                    info = baen.current.humidity;
                    weatherBean.humidity = info.value + info.unit;
                    String weather = baen.current.weather;
                    int index = Arrays.asList(WeatherCodeUtil.XIAOMI_INDEXS).indexOf(weather);
                    if (index == -1) {
                        weatherBean.weather = WeatherCodeUtil.XIAOMI_NAMES[WeatherCodeUtil.XIAOMI_NAMES.length - 1];
                        weatherBean.weatherID = WeatherCodeUtil.WEATHER_IMGS[WeatherCodeUtil.WEATHER_IMGS.length - 1];
                    } else {
                        weatherBean.weather = WeatherCodeUtil.XIAOMI_NAMES[index];
                        weatherBean.weatherID = WeatherCodeUtil.WEATHER_IMGS[index];
                    }
                    if (onWeatherChanged != null)
                        onWeatherChanged.onReceiveValue(weatherBean);
                }

            }
        });
    }

    private void getWeatherByXiaomi(final String id, LocationDataBean location, final ValueCallback<String> callback){
        // 获取实时天气 （小米）
        String url = XIAOMI_WEATHER_API_P1 + location.getLatitude()
                + XIAOMI_WEATHER_API_P2 + location.getLongitude()
                + XIAOMI_WEATHER_API_P3 + id
                + XIAOMI_WEATHER_API_P4;
        OKHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Timber.d("" + e);
                // 换另外一个api
                if (callback != null)
                    callback.onReceiveValue(id);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                XiaoMiWeaherBaen baen = null;
                try {
                    baen = new Gson().fromJson(data, XiaoMiWeaherBaen.class);
                } catch (Exception e) {
                    Timber.d("xiaomi fail " + e);
                    if (callback != null)
                        callback.onReceiveValue(id);
                    return;
                }

                if (baen != null) {
                    WeatherBean weatherBean = new WeatherBean();
                    XiaoMiWeaherBaen.Info1 info = baen.current.feelsLike;
                    weatherBean.temperature = info.value + info.unit;
                    info = baen.current.humidity;
                    weatherBean.humidity = info.value + info.unit;
                    String weather = baen.current.weather;
                    int index = Arrays.asList(WeatherCodeUtil.XIAOMI_INDEXS).indexOf(weather);
                    if (index == -1) {
                        weatherBean.weather = WeatherCodeUtil.XIAOMI_NAMES[WeatherCodeUtil.XIAOMI_NAMES.length - 1];
                        weatherBean.weatherID = WeatherCodeUtil.WEATHER_IMGS[WeatherCodeUtil.WEATHER_IMGS.length - 1];
                    } else {
                        weatherBean.weather = WeatherCodeUtil.XIAOMI_NAMES[index];
                        weatherBean.weatherID = WeatherCodeUtil.WEATHER_IMGS[index];
                    }
                    if (onWeatherChanged != null)
                        onWeatherChanged.onReceiveValue(weatherBean);
                }

            }
        });
    }

    private void getWeatherByWNL(final String id, LocationDataBean location, final ValueCallback<String> callback){
        String url = WNL_WEATHER_API + id;
//        String url = WEATHER_API_ZS + id + HTML;
        OKHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Timber.i("onFailure " + e);
                if (callback != null)callback.onReceiveValue(id);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                //解析返回值

                WnlWeatherBean bean = null;
                try {
                    XStream xStream = new XStream(new DomDriver());
                    xStream.ignoreUnknownElements();
                    // 尽量限制所需的最低权限 这条语句解决该问题
                    xStream.addPermission(AnyTypePermission.ANY);
                    xStream.alias("resp", WnlWeatherBean.class);
                    xStream.alias("weather", WnlWeatherBean.Weather.class);
                    bean = (WnlWeatherBean) xStream.fromXML(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bean != null) {
                    Timber.i("getWeatherByWNL ??");
                    WeatherBean weatherBean = new WeatherBean();
                    weatherBean.temperature = bean.wendu + "℃";
                    weatherBean.humidity = bean.shidu;
                    if (bean.forecast != null && bean.forecast.size()> 0) {
                        // 判断当前时间
                        Date date = new Date(System.currentTimeMillis());
                        String weather = "";
                        if (date.getHours() > 18) {
                            weather = bean.forecast.get(0).day.type;
                        } else weather = bean.forecast.get(0).night.type;
                        int index = Arrays.asList(WeatherCodeUtil.XIAOMI_NAMES).indexOf(weather);
                        if (index == -1) {
                            weatherBean.weather = WeatherCodeUtil.XIAOMI_NAMES[WeatherCodeUtil.XIAOMI_NAMES.length - 1];
                            weatherBean.weatherID = WeatherCodeUtil.WEATHER_IMGS[WeatherCodeUtil.WEATHER_IMGS.length - 1];
                        } else {
                            weatherBean.weather = weather;
                            weatherBean.weatherID = WeatherCodeUtil.WEATHER_IMGS[index];
                        }
                    }
                    if (onWeatherChanged != null)
                        onWeatherChanged.onReceiveValue(weatherBean);
                }
            }
        });
    }

    /**
     * 获取中国城市相关编号
     * @param location
     * @param address
     * @return
     */
    private String getChinaCityID(Location location, Address address){
        if (cityJsonObject == null)return null;
//        try {
//            String admin = removeLast(address.getAdminArea());
//            if (cityJsonObject.has(admin)) {
//                String data_sub_admin = cityJsonObject.getString(admin);
//                String sub_admin = removeLast(address.getSubAdminArea());
//                JSONObject jo2 = new JSONObject(data_sub_admin);
//                if (jo2.has(sub_admin)) {
//                    String data_locality = jo2.getString(sub_admin);
//                    String locality = removeLast(address.getLocality());
//                    JSONObject jo3 = new JSONObject(data_locality);
//                    if (jo3.has(locality)){
//                        String data_info = jo3.getString(locality);
//                        JSONObject jo4 = new JSONObject(data_info);
//                        if (jo4.has(AREAID)) {
//                            return jo4.get(AREAID).toString();
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        return getChinaCityID(address.getAdminArea(), address.getSubAdminArea(), address.getLocality());
    }

    private String getChinaCityID(String admin, String subAdmin, String locality){
        try {
            admin = removeLast(admin);
            subAdmin = removeLast(subAdmin);
            locality = removeLast(locality);
            if (cityJsonObject.has(admin)) {
                String data_sub_admin = cityJsonObject.getString(admin);
                JSONObject jo2 = new JSONObject(data_sub_admin);
                if (jo2.has(subAdmin)) {
                    String data_locality = jo2.getString(subAdmin);
                    JSONObject jo3 = new JSONObject(data_locality);
                    String data_info = null;
                    if (jo3.has(locality)){
                        data_info = jo3.getString(locality);
                    } else if (jo3.has(subAdmin)) {
                        data_info = jo3.getString(subAdmin);
                    }
                    if (data_info != null) {
                        JSONObject jo4 = new JSONObject(data_info);
//                        Timber.d(data_info + " " + );
                        if (jo4.has(AREAID)) {
                            return jo4.get(AREAID).toString();
                        }
                    }
                } else if (jo2.has(locality)) {
                    String data_locality = jo2.getString(locality);
                    Timber.d(data_locality);
                    JSONObject jo3 = new JSONObject(data_locality);
                    String data_info = null;
                    if (jo3.has(locality)){
                        data_info = jo3.getString(locality);
                    } else if (jo3.has(subAdmin)) {
                        data_info = jo3.getString(subAdmin);
                    }
                    if (data_info != null) {
                        JSONObject jo4 = new JSONObject(data_info);
//                        Timber.d(data_info + " " + );
                        if (jo4.has(AREAID)) {
                            return jo4.get(AREAID).toString();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void dispose(){
        if (locationUtil != null)
            locationUtil.dispose();
    }

    /**
     * 删除定位最后一位的省市
     * @param data
     * @return
     */
    private String removeLast(String data){
        data = data.substring(0, data.length() - 1);
        return data;
    }
}
