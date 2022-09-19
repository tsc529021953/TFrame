package com.nbhope.phmina.bean.request


/**
 *Created by ywr on 2021/6/24 8:59
 * 仿ntp计算 发起端和接收端系统时差
 * 发起端和接收端系统时差的计算方程为   ((crc-ssc)+(csc-src))/2
 */
class NtpTimeParams : BaseParams() {
    var ssc: Long = 0 //发起端当前时间
    var crc: Long = 0//接收端收到数据的当前时间
    var csc: Long = 0 //接收端发送时的时间
    var src: Long = 0 //发起端收到数据的当前时间

    /**
     * 发起端相对于接收端的系统时差
     *可能存在的问题，如果两次网络数据传输的延时差别较大  时间就不会不准确
     *
     */
    fun getSCSystemTimeDiff(): Long {
        return ((crc - ssc) + (csc - src)) / 2
    }

    /**
     * 一次校时网络总延时
     */
    fun getDelayMean(): Long {
        return src - ssc + csc - src
    }

}