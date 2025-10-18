package com.sc.common.utils;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class X5WebView extends WebView {
    private WebViewClient client = new WebViewClient() {
        // 防止调用其他浏览器
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            webView.loadUrl(url);
            return true;
        }
    };
    public X5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setWebViewClient(client);
        initWebViewSettings();
    }

    public X5WebView(Context context) {
        super(context);
        setBackgroundColor(85621);
    }
    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);														//支持JS
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);									//通过JS打开新的窗口
        webSetting.setAllowFileAccess(true);														//设置可以访问文件
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);							    //适应内容大小
        webSetting.setSupportZoom(true);															//支持缩放
        webSetting.setBuiltInZoomControls(true);													//设置内置的缩放控件
        webSetting.setUseWideViewPort(true);														//将图片调整到适合webview的大小
        webSetting.setSupportMultipleWindows(true);													//设置webview是否支持多窗口
        // webSetting.setLoadWithOverviewMode(true);												//缩放至屏幕大小
        webSetting.setAppCacheEnabled(true);														//设置是否应该启用应用程序缓存API。默认值为false。注意，为了启用应用程序缓存API，还必须向setAppCachePath(String)提供有效的数据库路径。
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);														//设置是否启用DOM存储API
        webSetting.setGeolocationEnabled(true);														//设置是否启用地理定位
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);												//设置应用程序缓存内容的最大大小
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);								//告诉WebView按需启用、禁用或拥有插件
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染优先级
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);											//设置缓存方式

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
        //5.0以后解决WebView加载的链接为Https开头，但链接里的内容，如图片为http链接，图片会加载不出来
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //两者都可以
            webSetting.setMixedContentMode(webSetting.getMixedContentMode());
            //mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }
}
