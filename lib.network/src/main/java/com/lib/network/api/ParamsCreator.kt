package com.lib.network.api

import com.google.gson.GsonBuilder
import com.lib.network.utils.HopeDesUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

object ParamsCreator {
    private const val TEXT_PLAIN = "text/plain"
    private const val KEY = "A716A953593940D2BD78E1A02CD3C070"
    private const val CID = "750064224428658688"
    private const val SID = "750837261197414400"
    private const val VER = "1.0"
    private val gson = GsonBuilder().disableHtmlEscaping().create()

    fun generateParams(dat: String): Map<String, String> {
        val params = HashMap<String, String>()
        params["key"] = KEY
        params["cid"] = CID
        params["sid"] = SID
        params["ver"] = VER
        params["des"] = calcDes(dat)
        params["dat"] = dat
        params["len"] = dat.length.toString()
        return params
    }

    fun generateParams(dat: Int): Map<String, String> {
        return generateParams(dat.toString())
    }

    fun generateParams(obj: Any): Map<String, String> {
        val dat = gson.toJson(obj)
        return generateParams(dat)
    }

    fun generateMultipartParams(dat: String): Map<String, RequestBody> {
        val params = HashMap<String, RequestBody>()
        params["key"] = createRequestBody(KEY)
        params["cid"] = createRequestBody(CID)
        params["sid"] = createRequestBody(SID)
        params["ver"] = createRequestBody(VER)
        params["des"] = createRequestBody(calcDes(dat))
        params["dat"] = createRequestBody(dat)
        params["len"] = createRequestBody(dat.length.toString())
        return params
    }

    fun generateRequestBodyParams(dat: String): Map<String, String> {
        val params = HashMap<String, String>()
        params["key"] = KEY
        params["cid"] = CID
        params["sid"] = SID
        params["ver"] = VER
        params["des"] = calcDes(dat)
        params["dat"] = dat
        params["len"] = dat.length.toString()
        return params
    }

//    fun generateRequestBodyParams(dat: Int): Map<String, RequestBody> {
//        return generateRequestBodyParams(dat.toString())
//    }

    fun generateMultipartParams(obj: Any): Map<String, RequestBody> {
        val dat = gson.toJson(obj)
        return generateMultipartParams(dat)
    }

    fun generateRequestBodyParams(obj: Any): Map<String, String> {
        val dat = gson.toJson(obj)
        return generateRequestBodyParams(dat)
    }

    private fun createRequestBody(content: String): RequestBody {
        return RequestBody.create(MediaType.parse(TEXT_PLAIN), content)
    }

    private fun calcDes(dat: String): String {
        return HopeDesUtil.calc(dat, KEY, CID, SID, VER)
    }



    fun generateRequestBodyParams2(dat: String): Map<String, RequestBody> {
        val params = HashMap<String, RequestBody>()
        params["key"] = createRequestBody(KEY)
        params["cid"] = createRequestBody(CID)
        params["sid"] = createRequestBody(SID)
        params["ver"] = createRequestBody(VER)
        params["des"] = createRequestBody(calcDes(dat))
        params["dat"] = createRequestBody(dat)
        params["len"] = createRequestBody(dat.length.toString())
        return params
    }

    fun generateRequestBodyParams2(obj: Any): Map<String, RequestBody> {
        val dat = gson.toJson(obj)
        return generateRequestBodyParams2(dat)
    }
}