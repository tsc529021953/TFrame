package com.nbhope.lib_frame.utils

import android.util.Log
import org.slf4j.LoggerFactory
import timber.log.Timber.DebugTree

class ThreadAwareDebugTree : DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        var tag: String? = tag
        if (tag != null) {
            val threadName = Thread.currentThread().name
            tag = "<$threadName> $tag"
        }

//        if (HopeUtils.isRemoteProcess(HopeUtils.getProcessName(HopeBaseApp.getContext()))){
//            tag = "<remote> $tag"
//        }

        val logMessage = "$tag: $message"
        when (priority) {
            Log.VERBOSE -> mLogger.trace(logMessage)
            Log.DEBUG -> mLogger.debug(logMessage)
            Log.INFO -> mLogger.info(logMessage)
            Log.WARN -> mLogger.warn(logMessage)
            Log.ERROR -> mLogger.error(logMessage)
        }

        super.log(priority, tag, message, t)
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        return super.createStackElementTag(element) + "(Line " + element.lineNumber + ")" //日志显示行号
    }

    companion object {
        var mLogger = LoggerFactory.getLogger(ThreadAwareDebugTree::class.java)
    }
}