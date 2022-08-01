package com.sc.lib_weather.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * @author sc
 * @date 2021/9/26 14:08
 */
public class AppJsonFileReader {
    public static String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static List<Map<String, String>> setData(String str) {
        try {
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            JSONArray array = new JSONArray(str);
            int len = array.length();
            Map<String, String> map;
            for (int i = 0; i < len; i++) {
                JSONObject object = array.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("operator", object.getString("operator"));
                map.put("loginDate", object.getString("loginDate"));
                map.put("logoutDate", object.getString("logoutDate"));
                data.add(map);
            }
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<Map<String, String>> setListData(String str) {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        try {

            JSONArray array = new JSONArray(str);
            int len = array.length();
            Map<String, String> map;
            for (int i = 0; i < len; i++) {
                JSONObject object = array.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("imageId", object.getString("imageId"));
                map.put("title", object.getString("title"));
                map.put("subTitle", object.getString("subTitle"));
                map.put("type", object.getString("type"));
                data.add(map);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;

    }

    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
//        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
//                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
//            Log.e("TestFile", "Error on write File:" + e);
            e.printStackTrace();
        }
    }
}
