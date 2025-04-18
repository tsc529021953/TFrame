//src/main/java/translate/TranslateConfig.java
package com.sc.tmp_translate.utils.hs;

import com.volcengine.helper.Const;
import com.volcengine.model.*;
//import org.apache.http.Header;
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TranslateConfig {
    public static String accessKey = "";
    public static String secretKey = "";
    public static String api = "SpeechTranslate";
    public static String path = "/api/translate/speech/v1/";
    public static String host = "translate.volces.com";

    public static ServiceInfo serviceInfo = new ServiceInfo(
            new HashMap<String, Object>() {
                {
                    put(Const.CONNECTION_TIMEOUT, 5000);
                    put(Const.SOCKET_TIMEOUT, 5000);
                    put(Const.Host, host);
                    put(Const.Header, new ArrayList<Header>() {
                        {
                            add(new Header("Accept", "application/json"));
                        }
                    });
                    put(Const.Credentials, new Credentials(Const.REGION_CN_NORTH_1, "translate"));
                }
            }
    );
    public static Map<String, ApiInfo> apiInfoList = new HashMap<String, ApiInfo>() {
        {
            put(api, new ApiInfo(
                    new HashMap<String, Object>() {
                        {
                            put(Const.Method, "GET");
                            put(Const.Path, path);
                            put(Const.Query, new ArrayList<NameValuePair>() {
                                {
                                    add(new NameValuePair("Action", api));
                                    add(new NameValuePair("Version", "2020-06-01"));
                                }
                            });
                        }
                    }
            ));
        }
    };
}
