package com.lib.network.vo

data class PayMentInfo2(
    val payment: List<Payment>,
    val service: List<Service>
)

data class Payment(
    val channelIcon: String,
    val channelId: Long,
    val channelMark: String,
    val channelName: String,
    val channelNo: String,
    val channelOrder: Int
)

data class Service(
    val prices: List<Price>,
    val serviceIcon: String,
    val serviceId: Long,
    val serviceMark: String,
    val serviceName: String,
    val serviceNo: String,
    val servicePrice: Int,
    val serviceStatus: Int
)

data class Price(
    val deleteRemarks: Boolean,
    val discountRemarks: String,
    val firstOpen: Boolean,
    val refrenceId: Long,
    val serviceDays: Int,
    val serviceOrder: Int,
    val servicePrice: Int,
    val serviceRemarks: String,
    val serviceTitle: String
)