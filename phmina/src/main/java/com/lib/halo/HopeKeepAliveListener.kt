package com.lib.halo

import android.os.Build
import com.ethanco.halo.turbo.ads.IKeepAliveListener
import com.ethanco.halo.turbo.ads.ISession
import com.ethanco.halo.turbo.bean.KeepAlive
import com.google.gson.Gson
import com.nbhope.phmina.base.MinaConstants
import com.nbhope.phmina.base.Utils
import com.nbhope.phmina.bean.data.Cmd
import com.nbhope.phmina.bean.request.HeartBeatParams
import timber.log.Timber

/**
 *Created by ywr on 2021/6/26 11:07
 */
class HopeKeepAliveListener : IKeepAliveListener {
    companion object {
        const val HEART_BEAT = "HEART_BEAT"
    }

    override fun onKeepAliveRequestTimedOut(keepAlive: KeepAlive?, iSession: ISession?) {
        Timber.i("????? 客戶onKeepAliveRequestTimedOut $iSession")
        iSession?.close()
    }

    override fun isKeepAliveMessage(ioSession: ISession?, message: Any?): Boolean {
        if (message == null) return false
        return if (message is String) {
            var res = message.toString().contains(MinaConstants.HEART_BEAT) && !message.toString().contains(Utils.getSn())
            if (res)
                Timber.i("????? 客戶心跳... $res $message ")
            res
        } else false
    }

    override fun getKeepAliveMessage(ioSession: ISession?, o: Any?): Any {
        Timber.i("????? 回复心跳... ")
        return Gson().toJson(
            Cmd(
                MinaConstants.HEART_BEAT,
                HeartBeatParams().also { it.hopeSn = Utils.getSn() })
        )
    }
}