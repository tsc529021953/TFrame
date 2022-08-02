package com.sc.xwservice.xp;

import android.os.Handler;
import com.sc.lib_system.sp.SerialPortUtil;
import com.sc.xwservice.util.DateUtil;
import com.sc.xwservice.xp.bean.XPBean;
import timber.log.Timber;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class XPUtil {

    private static final int TIMER = 30000;

    private SerialPortUtil serialPortUtil;

    String[] weekDays_CN = {"日", "一", "二", "三", "四", "五", "六"};

    String[] weathers = {"晴", "风", "云", "沙", "雨", "雾", "雪", "阴"};

    List<String> weathersList;

    Handler handler;
    Runnable runnable;

    boolean first = false;

    private XPBean xpBean, recordBean;

    public XPBean getXpBean() {
        return xpBean;
    }

    public XPUtil() {
        init();
    }

    void init() {
        weathersList = Arrays.asList(weathers);
        xpBean = new XPBean();
        recordBean = new XPBean();
        // ttyS0 ttyAMA2
        serialPortUtil = new SerialPortUtil("/dev/ttyAMA2", 115200);
        boolean res = serialPortUtil.open();
        Timber.d("res " + res);
        xpBean.tem = "30";
        xpBean.location = "宁波市";
        xpBean.weather = "多云";
        // 开定时器 定时刷新数据过去 40s刷新一次
        updateInfo();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, TIMER);
                updateInfo();
            }
        };
        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(runnable, TIMER);
        }
    }

    public void updateInfo() {
        Timber.d("Before刷新数据！" + xpBean.toString());
        if (serialPortUtil == null || !serialPortUtil.isOpen()) return;
        serialPortUtil.writeHex("FFFFFF");
        if (!first) {
            first = true;
            write("dim", 100 + "");
            Timber.d("设置亮度！");
        }
        Calendar date = Calendar.getInstance();
        xpBean.year = date.get(Calendar.YEAR);
//        writeStr("year", xpBean.year);
        xpBean.month = date.get(Calendar.MONTH) + 1;
//        if (xpBean.month != recordBean.month)
            writeStr("month", xpBean.month < 10 ? "0" + xpBean.month : xpBean.month + "");
        xpBean.day = date.get(Calendar.DAY_OF_MONTH);
        if (xpBean.day != recordBean.day)
            writeStr("day", xpBean.day < 10 ? "0" + xpBean.day : xpBean.day + "");
        xpBean.hour = date.get(Calendar.HOUR_OF_DAY);
        if (xpBean.hour != recordBean.hour) {
            writeStr("hour", xpBean.hour < 10 ? "0" + xpBean.hour : xpBean.hour + "");
            writeNum("hour_val", xpBean.hour + "");
        }
        xpBean.minutes = date.get(Calendar.MINUTE);
        if (xpBean.minutes != recordBean.minutes) {
            writeStr("minutes", xpBean.minutes < 10 ? "0" + xpBean.minutes : xpBean.minutes + "");
            writeNum("minutes_val", xpBean.minutes + "");
            Timber.d("刷新分钟 " + xpBean.minutes);
        }
        xpBean.second = date.get(Calendar.SECOND);
        if (xpBean.second != recordBean.second)
            writeStr("second_val", xpBean.second + "");
        xpBean.week = weekDays_CN[DateUtil.getWeek(new Date())];
        if (xpBean.week != recordBean.week)
            writeStr("week", xpBean.week);
        if (xpBean.tem != recordBean.tem)
            writeNum("tem", xpBean.tem);
        if (xpBean.location != recordBean.location)
            writeStr("location", xpBean.location);
        if (xpBean.weather != recordBean.weather)
            writeStr("weather", xpBean.weather);
        xpBean.weatherIndex = weathersList.indexOf(xpBean.weather);
        if (xpBean.weatherIndex == -1) {
            xpBean.weatherIndex = 0;
            for (int i = 0; i < weathersList.size(); i++) {
                if (xpBean.weather.contains(weathersList.get(i))) {
                    xpBean.weatherIndex = i;
                    continue;
                }
            }
        }
        if (xpBean.weatherIndex != recordBean.weatherIndex)
            writePic("main", xpBean.weatherIndex + "");
        Timber.d("刷新数据！" + xpBean.toString());
        recordBean.copy(xpBean);
    }

    public void write(String key, String value){
        serialPortUtil.writeStr(key + "=" + value);
        serialPortUtil.writeHex("FFFFFF");
    }

    public void writeNum(String key, String value) {
        serialPortUtil.writeStr(key + ".val=" + value);
        serialPortUtil.writeHex("FFFFFF");
    }

    public void writePic(String key, String value) {
        serialPortUtil.writeStr(key + ".pic=" + value);
        serialPortUtil.writeHex("FFFFFF");
    }

    public void writeStr(String key, String value) {
        String txt = key + ".txt=\"" + value + "\"";
        try {
            serialPortUtil.write(txt.getBytes("gb2312"));
            serialPortUtil.writeHex("FFFFFF");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        if (serialPortUtil != null)
            serialPortUtil.dispose();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }
}
