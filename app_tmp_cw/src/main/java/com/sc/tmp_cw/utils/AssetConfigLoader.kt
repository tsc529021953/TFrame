package com.sc.tmp_cw.utils

import android.content.Context
import com.google.gson.Gson
import timber.log.Timber

/**
 * 应用配置数据类
 */
data class AppConfig(
    val app_name: String,
    val api_config: ApiConfig,
    val features: Features,
    val theme: ThemeConfig,
    val server: ServerConfig
)

data class ApiConfig(
    val base_url: String,
    val api_key: String,
    val timeout: Int,
    val retry_count: Int
)

data class Features(
    val enable_premium: Boolean,
    val enable_analytics: Boolean,
    val enable_push_notification: Boolean
)

data class ThemeConfig(
    val primary_color: String,
    val secondary_color: String,
    val theme_name: String
)

data class ServerConfig(
    val websocket_url: String,
    val mqtt_broker: String
)

/**
 * Assets 配置加载器
 * 自动根据当前风味加载对应的配置文件
 */
class AssetConfigLoader(private val context: Context) {
    
    private val gson = Gson()
    private var cachedConfig: AppConfig? = null
    
    companion object {
        private const val CONFIG_FILE = "app_config.json"
    }
    
    /**
     * 加载应用配置
     * 系统会自动根据当前风味选择对应的配置文件
     */
    fun loadConfig(): AppConfig {
        // 如果已缓存，直接返回
        cachedConfig?.let { return it }
        
        return try {
            val json = context.assets.open(CONFIG_FILE)
                .bufferedReader()
                .use { it.readText() }
            
            val config = gson.fromJson(json, AppConfig::class.java)
            cachedConfig = config
            
            Timber.i("✅ 配置加载成功 - 风味: ${FlavorConfigUtil.getFlavorType()}, API: ${config.api_config.base_url}")
            config
        } catch (e: Exception) {
            Timber.e(e, "❌ 配置加载失败")
            throw RuntimeException("Failed to load config from assets", e)
        }
    }
    
    /**
     * 获取 API Base URL
     */
    fun getApiBaseUrl(): String {
        return loadConfig().api_config.base_url
    }
    
    /**
     * 获取 API Key
     */
    fun getApiKey(): String {
        return loadConfig().api_config.api_key
    }
    
    /**
     * 获取超时时间
     */
    fun getTimeout(): Int {
        return loadConfig().api_config.timeout
    }
    
    /**
     * 检查是否启用高级功能
     */
    fun isPremiumEnabled(): Boolean {
        return loadConfig().features.enable_premium
    }
    
    /**
     * 检查是否启用分析
     */
    fun isAnalyticsEnabled(): Boolean {
        return loadConfig().features.enable_analytics
    }
    
    /**
     * 获取主题名称
     */
    fun getThemeName(): String {
        return loadConfig().theme.theme_name
    }
    
    /**
     * 获取主色调
     */
    fun getPrimaryColor(): String {
        return loadConfig().theme.primary_color
    }
    
    /**
     * 获取 WebSocket URL
     */
    fun getWebSocketUrl(): String {
        return loadConfig().server.websocket_url
    }
    
    /**
     * 获取 MQTT Broker 地址
     */
    fun getMqttBroker(): String {
        return loadConfig().server.mqtt_broker
    }
    
    /**
     * 清除缓存（配置更新时调用）
     */
    fun clearCache() {
        cachedConfig = null
    }
    
    /**
     * 打印配置信息（用于调试）
     */
    fun printConfigInfo() {
        val config = loadConfig()
        Timber.i("=== 应用配置信息 ===")
        Timber.i("应用名称: ${config.app_name}")
        Timber.i("风味类型: ${FlavorConfigUtil.getFlavorType()}")
        Timber.i("API URL: ${config.api_config.base_url}")
        Timber.i("主题: ${config.theme.theme_name}")
        Timber.i("高级功能: ${config.features.enable_premium}")
        Timber.i("==================")
    }
}
