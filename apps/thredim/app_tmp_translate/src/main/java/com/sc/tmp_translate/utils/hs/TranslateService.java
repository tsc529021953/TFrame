// src/main/java/translate/TranslateService.java
package com.sc.tmp_translate.utils.hs;

import com.volcengine.model.ApiInfo;
import com.volcengine.model.ServiceInfo;
import com.volcengine.service.BaseServiceImpl;

import java.util.Map;

public class TranslateService extends BaseServiceImpl {

    public TranslateService(ServiceInfo info, Map<String, ApiInfo> apiInfoList) {
        super(info, apiInfoList);
    }
}
