package com.lib.network.vo.response

/**
 * @Author qiukeling
 * @Date 2020/12/30-4:10 PM
 * @Email qiukeling@nbhope.cn
 */
data class WeatherResponse(
        val message: String,
        val result: Result?,
        val status: Int
)

data class Result(val now: Live?)

data class Live(
        val text: String,
        val temp: Int,
        val feels_like: Int,
        val rh: Int,
        val wind_class: String,
        val wind_dir: String,
        val uptime: String
)