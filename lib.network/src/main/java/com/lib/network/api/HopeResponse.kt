package com.lib.network.api

import java.io.Serializable


data class HopeResponse<T>(val `object`: T?,
                           val list: T?,
                           val rows: T?,
                           val method: String?,
                           val code: Int?,
                           val message: String?,
                           val desc: String?) : Serializable {
    override fun toString(): String {
        return "HopeResponse(`object`=$`object`, list=$list, rows=$rows, method=$method, code=$code, message=$message, desc=$desc)"
    }
}