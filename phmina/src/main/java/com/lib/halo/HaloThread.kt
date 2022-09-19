package com.lib.halo

import android.os.HandlerThread
import android.util.Log
import com.ethanco.halo.turbo.Halo
import com.ethanco.halo.turbo.ads.IHandler
import com.ethanco.halo.turbo.ads.ISession
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import timber.log.Timber
import java.lang.Exception

/**
 *Created by ywr on 2021/6/28 8:53
 */
abstract class HaloThread(name: String) :
        HandlerThread(name, 10), IHandler {
    val tag = "HaloThread"
    private var halo: Halo? = null
    private var mSession: ISession? = null


    override fun onLooperPrepared() {
        super.onLooperPrepared()
        halo = getHalo()
        if (!isRunning()) {
            startHaloRs(halo?.start())
        }
    }

    override fun quitSafely(): Boolean {
        halo?.stop()
        return super.quitSafely()
    }

    override fun quit(): Boolean {
        halo?.stop()
        return super.quit()
    }


    override fun sessionClosed(session: ISession?) {
        if (halo?.isRunning == false) {
            Log.i(tag, "收到sessionClosed")
        }

    }

    override fun sessionCreated(session: ISession?) {
        Log.i(tag, "收到sessionCreated")
    }

    override fun sessionOpened(session: ISession?) {
        Log.i(tag, "收到sessionOpened")
    }

    override fun messageSent(session: ISession?, message: Any?) {
        if (message is String &&message!=null) {
//            Log.i(tag, "发送messageSent $message")
        }
    }

    override fun messageReceived(session: ISession?, message: Any?) {
        try {
            if (message is String && message != null) {
                val receive = JsonParser().parse(message).asJsonObject
                onMessageReceiver(session, message, receive["cmd"].asString, receive.get("params"))
            }
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }


    open fun sendMsg(session: ISession?, msg: String, other: Any) {
        mSession?.write(msg)
    }

    abstract fun onMessageReceiver(
            session: ISession?,
            srcMsg: String,
            cmd: String,
            params: JsonElement
    )

    abstract fun getHalo(): Halo

    /**
     * mina 启动结果
     * @param rs true 成功  false  失败
     */
    abstract fun startHaloRs(rs: Boolean? = false)
    val textLineCodecFactory: Any
        get() {
            val codec = HaloTextLineCodecFactory()
            codec.encoderMaxLineLength = 1024 * 1000
            codec.decoderMaxLineLength = 1024 * 1000
            return codec
        }

    open fun isRunning(): Boolean {
        halo?.apply {
            return this.isRunning
        }
        return false
    }
}