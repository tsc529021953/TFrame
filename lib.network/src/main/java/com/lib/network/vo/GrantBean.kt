package com.lib.network.vo

data class GrantBean(
    val comNo: String,
    val createTime: Long,
    val delState: Boolean,
    val deviceKey: String,
    val deviceSN: String,
    val deviceUuid: String,
    val refrenceId: Long,
    val updateTime: Long
)