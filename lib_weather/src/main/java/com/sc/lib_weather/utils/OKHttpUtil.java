package com.sc.lib_weather.utils;

import okhttp3.*;

import java.io.IOException;

/**
 * @author sc
 * @date 2021/9/17 13:44
 */
public class OKHttpUtil {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public static Response post(String url, String json){
        return post(url, json, null);
    }

    public static Response post(String url, String json, Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        if (callback == null) {
            // 同步方式
            try {
                Response response = call.execute();
                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else call.enqueue(callback);
        return null;
    }

    public static Response get(String url) {
        return get(url, null);
    }

    public static Response get(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        if (callback == null) {
            // 同步方式
            try {
                Response response = call.execute();
                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else call.enqueue(callback);
        return null;
    }

}
