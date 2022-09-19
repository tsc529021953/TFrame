package com.nbhope.lib_frame.constants

import kotlinx.coroutines.*
import timber.log.Timber

/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
object GlobalScopeUtils {
    fun launchSafety(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        timeMillis: Long = 10000,
        timeOutOrErrorBlock:( suspend CoroutineScope.(e:Exception?)->Unit) ? = null,
        block: suspend CoroutineScope.() -> Unit
    ){
        var job: Job? = null
        job = GlobalScope.launch(dispatcher){
            try {
                withTimeout(timeMillis){
                    block.invoke(this)
                }
            }catch (e:Exception){
                Timber.d("全局Global调用出现异常:"+e?.message)
                timeOutOrErrorBlock?.invoke(this,e)
            }finally {
//                Timber.d("全局Global调用结束")
                job?.cancel()
                job =null
            }
        }
    }
}