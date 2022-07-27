package com.sc.lib_frame.base

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sc.lib_frame.bus.event.OnceOnlyEvent
import kotlinx.coroutines.*
import com.sc.lib_frame.BuildConfig
import retrofit2.Response
import timber.log.Timber
import java.util.*

/**
 * Created by zhouwentao on 2019-08-30.
 */
open class BaseViewModel : ViewModel() {

    object ParameterField {
        var PATH = "PATH"
        var BUNDLE = "BUNDLE"
    }


    val mException: MutableLiveData<Exception> = MutableLiveData()

    val mOnceOnlyLiveData = OnceOnlyLiveData()

    private fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {

        viewModelScope.launch { block() }

    }

    /**
     * 在IO线程中启动协程 不可进行UI操作
     * 推荐在这启动协程 如果要在协程中操作UI
     * 可以
     * withContext(Dispatchers.Main){
            //TODO UI操作
     * }
     */
    fun launchIO(tryBlock: suspend CoroutineScope.() -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            tryCatch(tryBlock,{},{},true)
        }
    }


    /**
     * 在UI操作中启动协程 可以同时进行UI操作
     * 不推荐耗时或数据处理类方法在此操作 会有卡顿
     */
    fun launch(tryBlock: suspend CoroutineScope.() -> Unit) {
        launchOnUI {
            tryCatch(tryBlock, {}, {}, true)
        }
    }




    /**
     * 完整的tryCatch方法
     * 类似rxjava的 next error complete
     *
     * @param handleCancellationExceptionManually
     * 传false会把CancellationException的错误throw
     * 其他错误吞掉 使用mException暴露出
     */
    fun launchOnUITryCatch(tryBlock: suspend CoroutineScope.() -> Unit,
                           catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
                           finallyBlock: suspend CoroutineScope.() -> Unit,
                           handleCancellationExceptionManually: Boolean
    ) {
        launchIO {
            tryCatch(tryBlock, catchBlock, finallyBlock, handleCancellationExceptionManually)
        }
    }

    fun launchOnIOTryCatch(tryBlock: suspend CoroutineScope.() -> Unit,
                           catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
                           finallyBlock: suspend CoroutineScope.() -> Unit,
                           handleCancellationExceptionManually: Boolean
    ) {
        launchOnUI {
            tryCatch(tryBlock, catchBlock, finallyBlock, handleCancellationExceptionManually)
        }
    }


    private suspend fun tryCatch(
            tryBlock: suspend CoroutineScope.() -> Unit,
            catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
            finallyBlock: suspend CoroutineScope.() -> Unit,
            handleCancellationExceptionManually: Boolean = false) {
        coroutineScope {
            try {
                tryBlock()
            } catch (e: Exception) {
                if (e !is CancellationException || handleCancellationExceptionManually) {
                    mException.postValue(e)
                    catchBlock(e)
                    if (BuildConfig.DEBUG){
                        Timber.e(e)
                    }else{
                        //TODO 生产错误处理
                    }
                } else {
                    throw e
                }
            } finally {
                finallyBlock()
            }
        }
    }

    suspend fun executeResponse(response: Response<Any>, successBlock: suspend CoroutineScope.() -> Unit,
                                errorBlock: suspend CoroutineScope.() -> Unit) {
        coroutineScope {
            //            if (response.errorCode == -1) errorBlock()
//            else successBlock()
        }
    }

    fun showMessage(msg: String? = "未知错误！！") {
        mOnceOnlyLiveData.showMessage.postValue(OnceOnlyEvent(msg!!))
    }

    fun showDialog() {
        showDialog("加载中")
    }

    fun showDialog(msg: String) {
        mOnceOnlyLiveData.showDialogEvent.postValue(OnceOnlyEvent(msg))
    }

    fun dismissDialog() {
        mOnceOnlyLiveData.dismissDialogEvent.postValue(OnceOnlyEvent(""))
    }

    fun startActivity(arouterPath: String, bundle: Bundle? = null) {
        val params = HashMap<String, Any>()
        params[ParameterField.PATH] = arouterPath
        if (bundle != null) {
            params[ParameterField.BUNDLE] = bundle
        }
        mOnceOnlyLiveData.startActivityEvent.postValue(OnceOnlyEvent(params))
    }

    fun finish() {
        mOnceOnlyLiveData.finishEvent.postValue(OnceOnlyEvent(""))
    }

    fun clearTopActivityEvent(clazz: Class<BaseActivity>) {
        mOnceOnlyLiveData.clearTopActivityEvent.postValue(OnceOnlyEvent(clazz.simpleName))
    }

    fun backEvent() {
        mOnceOnlyLiveData.backEvent.postValue(OnceOnlyEvent(""))
    }


    inner class OnceOnlyLiveData {

        val showMessage = MutableLiveData<OnceOnlyEvent<String>>()

        val showDialogEvent = MutableLiveData<OnceOnlyEvent<String>>()

        val dismissDialogEvent = MutableLiveData<OnceOnlyEvent<String>>()

        val startActivityEvent = MutableLiveData<OnceOnlyEvent<Map<String, Any>>>()

        val finishEvent = MutableLiveData<OnceOnlyEvent<String>>()

        val clearTopActivityEvent = MutableLiveData<OnceOnlyEvent<String>>()

        val backEvent = MutableLiveData<OnceOnlyEvent<String>>()

    }

}