package com.sc.lib_weather.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.gson.Gson;
import com.sc.lib_weather.bean.LocationBean;
import com.sc.lib_weather.bean.LocationDataBean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;

/**
 * author: sc
 * date: 2021/8/24
 * 1. 权限申请
 * 2. 初始化 首先网络定位 次之GPS定位
 * 3. 监听网络变化（网络开启，判断是否已经初始化， 否， 初始化网络定位）
 * 4. 监听GPS变化（ GPS定位， 判断是否开启网络定位， 否，初始化GPS定位）
 * 5. 释放
 */
public class LocationUtil {

    // 180000
    public static final int REQUEST_CODE = 984
            // 半小时一次
            , MIN_TIMER = 1800000
            , MIN_DISTANCE = 100;

    private static boolean hasPermission = false;

    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };

    Context activity;

    LocationManager locationManager;

    Location location;

    private String locationProvider;

    private Criteria criteria;

    private ValueCallback<LocationDataBean> onLocationChanged;

    private int timer = MIN_TIMER, distance = MIN_DISTANCE;

    private Timer locationTimer;

    public LocationUtil(Context activity, ValueCallback<LocationDataBean> onLocationChanged) {
        this(activity, onLocationChanged, MIN_TIMER, MIN_DISTANCE);
    }

    public LocationUtil(Context activity, ValueCallback<LocationDataBean> onLocationChanged, int timer, int distance) {
        this.activity = activity;
        this.onLocationChanged = onLocationChanged;
        this.timer = timer;
        this.distance = distance;
        init();
    }

    private void init() {
        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        NetStateChangeReceiver.registerObserver(netStateChangeObserver);
        NetStateChangeReceiver.registerReceiver(activity.getApplicationContext());
        refresh();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 权限监听
        if (requestCode == REQUEST_CODE) {
            if (hasPermission(activity)){
                refresh();
            }
        }
    }

    public void refresh(){
        if (!hasPermission(activity))return;

//        openIPLocation();
//        if (activity != null)
//            return;
        // 遍历的方法
        String provider = null;
        List<String> providers = locationManager.getAllProviders();
        if (providers == null)return;
        // 异步运行
        if (!providers.contains(LocationManager.NETWORK_PROVIDER)
                && !providers.contains(LocationManager.GPS_PROVIDER)) {
            // 两个定位信息都不存在
            openIPLocation();
            return;
        }
        if (providers.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
            Timber.d("这里" + provider);
        }
        else {
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            //设置不需要获取海拔方向数据
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            //设置允许产生资费
            criteria.setCostAllowed(true);
            //要求低耗电
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            provider = locationManager.getBestProvider(criteria, false);
        }
        Timber.d("推荐 " + provider + " " + locationProvider + " " + providers.size());
        if (provider == null || provider.equals(locationProvider)) return;
        locationProvider = provider;
        Timber.d("we choose "+ locationProvider);

        // 注册
        registerListener(timer, distance);
    }

    public void dispose() {
        unregisterListener();
        if (netStateChangeObserver != null) {
            Timber.d("释放天气");
            NetStateChangeReceiver.unRegisterObserver(netStateChangeObserver);
            NetStateChangeReceiver.unRegisterReceiver(activity.getApplicationContext());
        }
        if (locationTimer != null)
            locationTimer.cancel();
    }

    public static boolean hasPermission(Context context) {
        return hasPermission(context, false);
    }

    public static boolean hasPermission(Context context, boolean request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> perList = new ArrayList<>();
            for (int i = 0; i < PERMISSIONS.length; i++) {
                if (context.checkSelfPermission(PERMISSIONS[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    perList.add(PERMISSIONS[i]);
                }
            }
//            if (perList.size() > 0) {
//                // 没有拿到权限
//                if (request) {
//                    context.requestPermissions(perList.toArray(new String[0]), REQUEST_CODE);
//                }
//                return false;
//            }
        }
        return true;
    }

    /**
     * 获取当前城市
     */
    public Location getLocalCity() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(activity
                    , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    activity.requestPermissions(PERMISSIONS, REQUEST_CODE);
                }
                return null;
            }
            if (locationProvider == null)return null;
            location = locationManager.getLastKnownLocation(locationProvider);
//            Timber.d(location.toString());
//            Timber.d(location.getLongitude() + " " + location.getAltitude());
            return location;
        }
        return null;
    }

    private void registerListener(int timer, int distance) {
        if (locationListener == null)return;
        if (locationProvider == null)return;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Timber.d("没有定位权限 申请！");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            // 获取当前定位
            location = locationManager.getLastKnownLocation(locationProvider);
            if (location == null) location = getLastKnownLocation();
            Timber.d("注册函数 " + location);
            locationManager.requestLocationUpdates(
                    locationProvider,
                    timer,
                    distance,
                    locationListener
            );

        }
    }

    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationManager locationManager = (LocationManager)activity.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        Location bestLocation = null;
        for (String provider : providers) {

            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void unregisterListener(){
        if (locationManager != null && locationListener != null){
            locationManager.removeUpdates(locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
//            Timber.d("onLocationChanged " + location);
            if (location == null)return;
            if (onLocationChanged != null) {
                // 异步进行
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Address address = getAddress(activity, location.getLatitude(), location.getLongitude());
                        if (address == null)return;
                        LocationDataBean bean = new LocationDataBean(location, address);
                        onLocationChanged.onReceiveValue(bean);
                    }
                });
                thread.start();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        /**
         * 定位服务开启
         * @param provider
         */
        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Timber.d("定位服务开启！");
            refresh();
        }

        /**
         * 定位服务关闭
         * @param provider
         */
        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Timber.d("定位服务关闭！");
//            refresh();
            // 被关闭，IP定位
            openIPLocation();
        }
    };

    /**
     * 监听当前网络状况
     */
    NetStateChangeReceiver.NetStateChangeObserver netStateChangeObserver
            = new NetStateChangeReceiver.NetStateChangeObserver() {
        @Override
        public void onNetDisconnected() {
            Timber.d("网络断开");
            refresh();
        }

        @Override
        public void onNetConnected(NetworkType networkType) {
            Timber.d("网络连接");
            refresh();
        }
    };

    /**
     * 根据经度纬度获取位置
     * @return
     */
    public static Address getAddress(Context context, double latitude, double longitude){
        Geocoder geocoder = new Geocoder(context);
        boolean flag = Geocoder.isPresent();
        try {
            List<Address> addresses = null;
            try {
                latitude = 29.828947;
                longitude = 121.60133;
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0){
//                Address
                // 此处编辑详细地址
                return addresses.get(0);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

//    https://geoip.com/whats-my-ip/ "https://freegeoip.app/json/"
    private static final String GEOIP_API = "http://ip-api.com/json/?lang=zh-CN";
    private static final String GEOIP1 = "https://freegeoip.app/json/";
    /**
     * 用于获取坐标所在城区
     */
    private static final String GEO_CITY = "https://whois.pconline.com.cn/ipAreaCoord.jsp?level=3&coords=";
    /*获取ip*/
    private static final String GEOIP2 = "https://api.hostip.info/get_json.php";
    /*根据ip获取经纬度*/
    private static final String GEO_LOC = "https://api.hostip.info/get_html.php?position=true&ip=12.215.42.19";
    /*淘宝相关定位*/
    private static final String GEO_CITY2 = "http://ip.aliyun.com/outGetIpInfo?accessKey=alibaba-inc&ip=";

    // 第三个用于跳过检测
    private boolean[] ipServiceStates = {
        true, true, false, true, true, false, true
    };

    /**
     * 开机IP定位定时器
     */
    private void openIPLocation(){
        if (locationTimer != null)return;
        locationTimer = new Timer();
        locationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 没有网络不做申请
                if (!NetworkUtil.isNetworkConnected(activity))return;
                getIPAPI(0, s -> getIP(1, s1 -> {
                    // 判断是否有ip
//                    if (s1 != null) {
//                        // 直接获取地址
//                        getCity2(s1, new ValueCallback<String>() {
//                            @Override
//                            public void onReceiveValue(String s) {
//
//                            }
//                        });
//                    } else {
//                        // 先获取ip 再获取地址
//
//                    }
                }));
            }
        },0, timer);
    }

    void getIPAPI(int i, ValueCallback<String> callback){
        Response(i, callback,  GEOIP_API , new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onReceiveValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                Timber.d("getIPAPI info " + data);
                LocationDataBean bean1 = null;
                try {
                    LocationBean.LocationIpApiBean bean = new Gson().fromJson(data, LocationBean.LocationIpApiBean.class);
                    if (bean == null) {
                        callback.onReceiveValue(null);
                        return;
                    }
                    bean1 = new LocationDataBean
                            (bean.country, bean.regionName, bean.city, bean.city
                                    , Double.parseDouble(bean.lat), Double.parseDouble(bean.lon));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bean1 != null && onLocationChanged != null) {
                    Timber.d("getIPAPI " + bean1.subAdmin);
                    onLocationChanged.onReceiveValue(bean1);
                } else {
                    callback.onReceiveValue(null);
                }
            }
        });
    }

    void getIP(int i, ValueCallback<String> callback){
        Response(i, callback, GEOIP1, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onReceiveValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                try {
                    LocationBean bean = new Gson().fromJson(data, LocationBean.class);
                    if (bean == null) {
                        callback.onReceiveValue(null);
                        return;
                    }
                    getCity(Double.parseDouble(bean.latitude), Double.parseDouble(bean.longitude), bean.ip, callback);
                    // 获取了数据
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onReceiveValue(null);
                    return;
                }
            }
        });
    }

    void getCity(double latitude, double longitude, String ip, ValueCallback<String> callback) {
        String url = GEO_CITY + longitude + "," + latitude;
        Response(3, callback, url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onReceiveValue(ip);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                LocationDataBean bean1 = null;
                // 解析数据
                if (data.contains("'")) {
                    String[] arr = data.split("'");
                    if (arr.length >= 2) {
                        String data2 = arr[1];
                        if (data2.contains(";")) {
                            String[] arr2 = data2.split(";");
                            if (arr2.length >= 3) {
                                String admin = arr2[0].split(",")[0];
                                String subAdmin = arr2[1].split(",")[0];
                                String locality = arr2[2].split(",")[0];
                                bean1 = new LocationDataBean(admin, subAdmin, locality, latitude, longitude);
                            }
                        }
                    }
                }
                if (bean1 != null && onLocationChanged != null) {
                    Timber.d("getCity " + bean1.subAdmin);
                    onLocationChanged.onReceiveValue(bean1);
                } else {
                    callback.onReceiveValue(ip);
                }
            }
        }, ip);
    }

    void getIP2(int i, ValueCallback<String> callback){
        Response(i, callback, GEOIP2, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onReceiveValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                try {
                    LocationBean.IPBean bean = new Gson().fromJson(data, LocationBean.IPBean.class);
                    if (bean == null) {
                        callback.onReceiveValue(null);
                        return;
                    }
                    getCity2(bean.ip, callback);
                    // 获取了数据
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onReceiveValue(null);
                    return;
                }
            }
        });
    }

    void getCity2(String ip, ValueCallback<String> callback) {
        String url = GEO_CITY + ip;
        Response(6, callback, url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onReceiveValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                LocationDataBean bean1 = null;
                Timber.d(data);
                // 解析数据
                if (data.contains("'")) {
                    String[] arr = data.split("'");
                    if (arr.length >= 2) {
                        String data2 = arr[1];
                        if (data2.contains(";")) {
                            String[] arr2 = data2.split(";");
                            if (arr2.length >= 3) {
                                String admin = arr2[0].split(",")[0];
                                String subAdmin = arr2[1].split(",")[0];
                                String locality = arr2[2].split(",")[0];
//                                bean1 = new LocationDataBean(admin, subAdmin, locality, latitude, longitude);
                            }
                        }
                    }
                }
                if (bean1 != null && onLocationChanged != null) {
                    onLocationChanged.onReceiveValue(bean1);
                } else {
                    callback.onReceiveValue(null);
                }
            }
        });
    }

    void Response(int i, ValueCallback<String> callback, String url, Callback callback2) {
        Response(i, callback, url, callback2, null);
    }

    void Response(int i, ValueCallback<String> callback, String url, Callback callback2, String ip){
        if (i > 0) ipServiceStates[i - 1] = false;
//        Timber.d(i + " " + ipServiceStates[i]);
        if (!ipServiceStates[i]) {
            callback.onReceiveValue(ip);
            return;
        }
        OKHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onReceiveValue(ip);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback2.onResponse(call, response);
            }
        });
    }
}
