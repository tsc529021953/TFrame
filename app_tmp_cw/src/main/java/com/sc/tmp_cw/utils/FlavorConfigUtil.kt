package com.sc.tmp_cw.utils

import android.util.Log
import com.sc.tmp_cw.BuildConfig

/**
 * 风味配置工具类
 * 用于在代码中判断当前打包的风味类型，执行不同的逻辑
 */
object FlavorConfigUtil {
    
    private const val TAG = "FlavorConfigUtil"
    
    /**
     * 获取当前风味类型
     * @return 风味类型字符串，如 "flavorA" 或 "flavorB"
     */
    fun getFlavorType(): String {
        return BuildConfig.FLAVOR_TYPE
    }
    
    /**
     * 判断是否为 Flavor A
     * @return true 如果是 Flavor A，否则 false
     */
    fun isFlavorA(): Boolean {
        return BuildConfig.IS_FLAVOR_A
    }
    
    /**
     * 根据风味执行不同的逻辑
     */
    fun executeByFlavor(flavorAAction: () -> Unit, flavorBAction: () -> Unit) {
        if (isFlavorA()) {
            Log.d(TAG, "执行 Flavor A 逻辑")
            flavorAAction()
        } else {
            Log.d(TAG, "执行 Flavor B 逻辑")
            flavorBAction()
        }
    }
    
    /**
     * 示例：根据不同风味加载不同的配置
     */
    fun getConfigValue(): String {
        return when (getFlavorType()) {
            "flavorA" -> {
                // Flavor A 的配置
                "https://api.flavora.example.com"
            }
            "flavorB" -> {
                // Flavor B 的配置
                "https://api.flavorb.example.com"
            }
            else -> {
                // 默认配置
                "https://api.default.example.com"
            }
        }
    }
    
    /**
     * 示例：根据不同风味启用不同的功能
     */
    fun isFeatureEnabled(featureName: String): Boolean {
        return when (getFlavorType()) {
            "flavorA" -> {
                when (featureName) {
                    "feature_x" -> true
                    "feature_y" -> false
                    else -> false
                }
            }
            "flavorB" -> {
                when (featureName) {
                    "feature_x" -> false
                    "feature_y" -> true
                    else -> false
                }
            }
            else -> false
        }
    }
}
