package com.sc.xwservice.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    String[] weekDays_CN = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public static int getWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return w;
    }
}
