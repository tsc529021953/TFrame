package com.sc.lib_weather.utils;

import com.sc.lib_weather.R;

import java.lang.reflect.Field;

/**
 * @author sc
 * @date 2021/9/26 16:39
 */
public class ImageUtil {

    public static int getimages(String name){
        if (name == null)return 0;
        Class drawable = R.drawable.class;
        Field field = null;
        try {
            field =drawable.getField(name);
            int images = field.getInt(field.getName());
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
