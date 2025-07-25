package com.nbhope.lib_frame.utils;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /**
     * 转换成00:00/05:00格式
     *
     * @param cur
     * @param total
     * @return
     */
    public static String toProgress(int cur, int total) {
        StringBuilder pStr = new StringBuilder();
        int min = cur / 60;
        int sec = cur % 60;
        if (min < 10) pStr.append(0);
        pStr.append(min);
        pStr.append(":");
        if (sec < 10) pStr.append(0);
        pStr.append(sec);
        pStr.append("/");

        min = total / 60;
        sec = total % 60;
        if (min < 10) pStr.append(0);
        pStr.append(min);
        pStr.append(":");
        if (sec < 10) pStr.append(0);
        pStr.append(sec);

        return pStr.toString();
    }

    /**
     * 转换成00:00-05:00格式
     *
     * @param start
     * @param end
     * @return
     */
    public static String toSegment(int start, int end) {
        StringBuilder pStr = new StringBuilder();
        int min = start / 60;
        int sec = start % 60;
        if (min < 10) pStr.append(0);
        pStr.append(min);
        pStr.append(":");
        if (sec < 10) pStr.append(0);
        pStr.append(sec);
        pStr.append("-");

        min = end / 60;
        sec = end % 60;
        if (min < 10) pStr.append(0);
        pStr.append(min);
        pStr.append(":");
        if (sec < 10) pStr.append(0);
        pStr.append(sec);

        return pStr.toString();
    }

    /**
     * 获取系统时间戳
     *
     * @return
     */
    public static long getCurTimeLong() {
        long time = System.currentTimeMillis();
        return time;
    }

    /**
     * 获取当前时间
     *
     * @param pattern
     * @return
     */
    public static String getCurDate(String pattern) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new Date());
    }

    public static String getDateToString(long milSecond) {
        return getDateToString(milSecond, "yyyy-MM-dd");
    }

    /**
     * 时间戳转换成字符窜
     *
     * @param milSecond
     * @param pattern
     * @return
     */
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 将字符串转为时间戳
     *
     * @param dateString
     * @param pattern
     * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static long timeToDurationMillis(String timeStr) {
        String[] parts = timeStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        return (hours * 3600 + minutes * 60 + seconds) * 1000L;
    }





}
