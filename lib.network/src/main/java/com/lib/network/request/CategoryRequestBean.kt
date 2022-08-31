package com.lib.network.request

import java.io.Serializable

/**
 * @Author qiukeling
 * @Date 2019-09-10-13:18
 * @Email qiukeling@nbhope.cn
 */
data class CategoryRequestBean(
        val authCode: String,
        val deviceId: Long,
        val deviceSN: String,
        val categoryId: Int) : Serializable