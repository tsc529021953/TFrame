package com.sc.lib_weather.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.ValueCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import com.sc.lib_weather.bean.LocationBean;
import com.sc.lib_weather.bean.LocationDataBean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.lionsoul.ip2region.xdb.Searcher;
import timber.log.Timber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;

//import org.lionsoul.ip2region.DataBlock;
//import org.lionsoul.ip2region.DbConfig;
//import org.lionsoul.ip2region.DbSearcher;
//import org.lionsoul.ip2region.Util;

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
            , MIN_TIMER = 1800000, MIN_DISTANCE = 100;

    private static boolean hasPermission = false;


    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
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

    private boolean isIPLocation = false;

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
            if (hasPermission(activity)) {
                refresh();
            }
        }
    }

    public void refresh() {
        if (!hasPermission(activity)) return;

//        openIPLocation();
//        if (activity != null)
//            return;
        // 遍历的方法
        String provider = null;
        List<String> providers = locationManager.getAllProviders();
        Timber.i("providers " + (providers == null ? null : providers.size()));
        // 异步运行
        if (providers == null || (!providers.contains(LocationManager.NETWORK_PROVIDER)
                && !providers.contains(LocationManager.GPS_PROVIDER))) {
            // 两个定位信息都不存在
            Timber.i("开启网络定位！");
            openIPLocation();
            isIPLocation = true;
            return;
        }
        isIPLocation = false;
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
            Timber.d("这里" + provider);
        } else {
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
        Timber.d("we choose " + locationProvider);

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
            if (perList.size() > 0) {
                // 没有拿到权限
//                if (request) {
//                    context.requestPermissions(perList.toArray(new String[0]), REQUEST_CODE);
//                }
                return false;
            }
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
            if (locationProvider == null) return null;
            location = locationManager.getLastKnownLocation(locationProvider);
//            Timber.d(location.toString());
//            Timber.d(location.getLongitude() + " " + location.getAltitude());
            return location;
        }
        return null;
    }

    private void registerListener(int timer, int distance) {
        if (locationListener == null) return;
        if (locationProvider == null) return;
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
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
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

    private void unregisterListener() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
//            Timber.d("onLocationChanged " + location);
            if (location == null) return;
            if (onLocationChanged != null) {
                // 异步进行
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Address address = getAddress(activity, location.getLatitude(), location.getLongitude());
                        if (address == null) return;
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
            if (isIPLocation)
                refreshLocation();
            else
                refresh();
        }
    };

    /**
     * 根据经度纬度获取位置
     *
     * @return
     */
    public static Address getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context);
        boolean flag = Geocoder.isPresent();
        try {
            List<Address> addresses = null;
            try {
//                latitude = latitude;
//                longitude = longitude;
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
//                Address
                // 此处编辑详细地址
                return addresses.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //    https://geoip.com/whats-my-ip/ "https://freegeoip.app/json/"
    private static final String GEOIP_API = "http://ip-api.com/json/?lang=zh-CN";
    private static final String GEOIP1 = "https://freegeoip.app/json/";
    private static final String GRO_CITY_3 = "https://whois.pconline.com.cn/ipJson.jsp";
    /**
     * 用于获取坐标所在城区
     */
    private static final String GEO_CITY = "https://whois.pconline.com.cn/ipAreaCoord.jsp?level=3&coords=";
    /*获取ip*/
    private static final String GEOIP2 = "https://api.hostip.info/get_json.php";
    /*根据ip获取经纬度*/
    private static final String GEO_LOC = "https://api.hostip.info/get_html.php?position=true&ip=";
    /*淘宝相关定位*/
    private static final String GEO_CITY2 = "http://ip.aliyun.com/outGetIpInfo?accessKey=alibaba-inc&ip=";

    // 第三个用于跳过检测
    private boolean[] ipServiceStates = {
            true, true, false, true, true, false, true
    };

    /**
     * 开机IP定位定时器
     */
    private void openIPLocation() {
        if (locationTimer != null) return;
        locationTimer = new Timer();
        locationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 没有网络不做申请
                refreshLocation();
            }
        }, 1000, timer);
    }

    private void refreshLocation() {
        if (!NetworkUtil.isNetworkConnected(activity)) return;
        Timber.i("刷新当前定位！");
        getIPAPI(0, s -> getCity3(1, s1 -> getIP(3, s2 -> {
        })));
    }

    void getIPAPI(int i, ValueCallback<String> callback) {
        Response(i, callback, GEOIP_API, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Timber.i("getIPAPI onFailure " + e);
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

    void getIP(int i, ValueCallback<String> callback) {
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
                    Timber.i("data " + data);
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

    void getIP2(int i, ValueCallback<String> callback) {
        Response(i, callback, GEOIP2, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onReceiveValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                Timber.i("onResponse " + data);
                try {
                    LocationBean.IPBean bean = new Gson().fromJson(data, LocationBean.IPBean.class);
                    if (bean == null) {
                        callback.onReceiveValue(null);
                        return;
                    }
//                    getCity3(bean.ip, callback);
                    // 获取了数据
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onReceiveValue(null);
                    return;
                }
            }
        });
    }

    void getLL(String ip, ValueCallback<String> callback) {
        String url = GEO_LOC + ip;
        Response(4, callback, url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onReceiveValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                LocationDataBean bean1 = null;
                Timber.d("getLL " + data);
//                // 解析数据
//                if (data.contains("'")) {
//                    String[] arr = data.split("'");
//                    if (arr.length >= 2) {
//                        String data2 = arr[1];
//                        if (data2.contains(";")) {
//                            String[] arr2 = data2.split(";");
//                            if (arr2.length >= 3) {
//                                String admin = arr2[0].split(",")[0];
//                                String subAdmin = arr2[1].split(",")[0];
//                                String locality = arr2[2].split(",")[0];
////                                bean1 = new LocationDataBean(admin, subAdmin, locality, latitude, longitude);
//                            }
//                        }
//                    }
//                }
//                if (bean1 != null && onLocationChanged != null) {
//                    onLocationChanged.onReceiveValue(bean1);
//                } else {
//                    callback.onReceiveValue(null);
//                }
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

    void getCity3(int i, ValueCallback<String> callback) {
        String url = GRO_CITY_3;
        Response(i, callback, url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onReceiveValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                Timber.i("getCity3 " +data);
                LocationDataBean bean1 = null;
                // 解析数据
                String[] arr = data.split("[\\(\\)]");
                Timber.i(""+arr.length);
                if (arr.length == 5) {
                    Timber.i(""+arr[3]);
                    LocationBean.LocationBean2 bean = new Gson().fromJson(arr[3], LocationBean.LocationBean2.class);
                    bean1 = new LocationDataBean(bean.pro, bean.city, bean.city);
                }
//                String va = "\\(\\";
//                Timber.i(""+data.contains(va));
//                if (data.contains(va)) {
//                    String[] arr = data.split(va);
//                    Timber.i(""+arr.length);
//                    if (arr.length >= 3) {
//                        String data2 = arr[2];
//                        va = "\\)";
//                        Timber.i(""+data.contains(va));
//                        if (data2.contains(va)) {
//                            String[] arr2 = data2.split(va);
//                            Timber.i(""+arr2.length);
//                            if (arr2.length == 2) {
//                                LocationBean.LocationBean2 bean = new Gson().fromJson(arr2[0], LocationBean.LocationBean2.class);
//                                bean1 = new LocationDataBean(bean.pro, bean.city, bean.city);
//                            }
//                        }
//                    }
//                }
                if (bean1 != null && onLocationChanged != null) {
                    Timber.d("getCity " + bean1.subAdmin);
                    onLocationChanged.onReceiveValue(bean1);
                } else {
                    callback.onReceiveValue(null);
                }
            }
        }, null);
//        // 判断路径是否存在
//        // 若目标文件夹不存在，则创建
//        String name = "ip2region.db";
//        String path = "/sdcard/";
////        if (Build.VERSION.SDK_INT > 29) {
////            path = activity.getExternalFilesDir(null).getAbsolutePath();
////        } else {
////            path = Environment.getExternalStorageDirectory().getPath();
////        }
//        ContextWrapper cw = new ContextWrapper(activity);
//        path = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/";
//        path += name;
//        Timber.i("路径 " + path);
//        File dir = new File(path);
//        if (!dir.exists()) {
//            Timber.d("路径不存在");
//            try {
//                InputStream inStream = activity.getAssets().open(name);
//                FileOutputStream fileOutputStream = new FileOutputStream(path);
//
//                int byteread;
//                byte[] buffer = new byte[1024];
//                while ((byteread = inStream.read(buffer)) != -1) {
//                    fileOutputStream.write(buffer, 0, byteread);
//                }
//                fileOutputStream.flush();
//                inStream.close();
//                fileOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Timber.d("路径存在");
//        }
//        try {
//            if (!dir.exists())return;
//            Searcher searcher = Searcher.newWithFileOnly(path);
//            String res = searcher.search(ip);
//            Timber.i("res "+ ip + " " + Searcher.checkIP(ip) + " " + res);
//            return;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (callback != null)
//            callback.onReceiveValue(null);
    }

    public static Searcher createSearcher(String dbPath, String cachePolicy) throws IOException {
        if ("file".equals(cachePolicy)) {
            return Searcher.newWithFileOnly(dbPath);
        } else if ("vectorIndex".equals(cachePolicy)) {
            byte[] vIndex = Searcher.loadVectorIndexFromFile(dbPath);
            return Searcher.newWithVectorIndex(dbPath, vIndex);
        } else if ("content".equals(cachePolicy)) {
            byte[] cBuff = Searcher.loadContentFromFile(dbPath);
            return Searcher.newWithBuffer(cBuff);
        } else {
            throw new IOException("invalid cache policy `" + cachePolicy + "`, options: file/vectorIndex/content");
        }
    }

//    /**
//     * 获取IP地址
//     * @param request
//     * @return
//     */
//    public static String getIpAddr(HttpServletRequest request) {
//        String ip = request.getHeader("x-forwarded-for");
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("X-Real-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//        }
//        if ("0:0:0:0:0:0:0:1".equals(ip)) {
//            ip = "127.0.0.1";
//        }
//        if (ip.split(",").length > 1) {
//            ip = ip.split(",")[0];
//        }
//        return ip;
//    }

    /**
     * 根据IP地址获取城市
     *
     * @param ip
     * @return
     */
    public static String getCityInfo(String ip) {
        return null;
//        URL url = IPUtil.class.getClassLoader().getResource("ip2region.db");
//        File file;
//        if (url != null) {
//            file = new File(url.getFile());
//        } else {
//            return null;
//        }
//        if (!file.exists()) {
//            System.out.println("Error: Invalid ip2region.db file, filePath：" + file.getPath());
//            return null;
//        }
//        //查询算法
//        int algorithm = DbSearcher.BTREE_ALGORITHM; //B-tree
//        //DbSearcher.BINARY_ALGORITHM //Binary
//        //DbSearcher.MEMORY_ALGORITYM //Memory
//        try {
//            DbConfig config = new DbConfig();
//            DbSearcher searcher = new DbSearcher(config, file.getPath());
//            Method method;
//            switch ( algorithm )
//            {
//                case DbSearcher.BTREE_ALGORITHM:
//                    method = searcher.getClass().getMethod("btreeSearch", String.class);
//                    break;
//                case DbSearcher.BINARY_ALGORITHM:
//                    method = searcher.getClass().getMethod("binarySearch", String.class);
//                    break;
//                case DbSearcher.MEMORY_ALGORITYM:
//                    method = searcher.getClass().getMethod("memorySearch", String.class);
//                    break;
//                default:
//                    return null;
//            }
//            DataBlock dataBlock;
//            if (!Util.isIpAddress(ip)) {
//                System.out.println("Error: Invalid ip address");
//                return null;
//            }
//            dataBlock  = (DataBlock) method.invoke(searcher, ip);
//            return dataBlock.getRegion();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    void Response(int i, ValueCallback<String> callback, String url, Callback callback2) {
        Response(i, callback, url, callback2, null);
    }

    void Response(int i, ValueCallback<String> callback, String url, Callback callback2, String ip) {
        if (i > 0) ipServiceStates[i - 1] = false;
        Timber.d(i + " " + ipServiceStates[i]);
        if (!ipServiceStates[i]) {
            callback.onReceiveValue(ip);
            return;
        }
        OKHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Timber.i("OKHttp onFailure " + e);
                callback.onReceiveValue(ip);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback2.onResponse(call, response);
            }
        });
    }
}
