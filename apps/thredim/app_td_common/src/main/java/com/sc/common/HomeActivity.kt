package com.sc.common

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.network.NetworkCallback
import com.nbhope.lib_frame.utils.AppUtil
import com.sc.common.databinding.ActivityHomeBinding
import com.sc.common.databinding.ActivityMainBinding
import com.sc.common.vm.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @author  tsc
 * @date  2024/4/25 17:47
 * @version 0.0.0-1
 * @description

 */
class HomeActivity : BaseBindingActivity<ActivityHomeBinding, MainViewModel>() {

    companion object {

        const val REQUEST_CODE = 10085

        var PERMISSIONS = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        const val PLAY_IMAGE_TIME = 15000L
        const val CTRL_LAYOUT_VIEW_TIME = 10000L

    }

    override var layoutId: Int = R.layout.activity_home

    @Inject
    override lateinit var viewModel: MainViewModel

    @Inject
    lateinit var networkCallback: NetworkCallback

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {

        }
    }

    override fun initParam(savedInstanceState: Bundle?) {
        super.initParam(savedInstanceState)
        hookWebView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.out.println("onCreate ??? $requestedOrientation ${requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}")
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

    }

    override fun subscribeUi() {
        binding.vm = viewModel
        if (checkPermissions(true)) {
            init()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && checkPermissions(false)) {
            System.out.println("onRequestPermissionsResult ")
            init()
        }
    }

    override fun initData() {
        // 加载url
//        var url = "https://qbi.yuhong.com.cn/token3rd/dashboard/view/pc.htm?pageId=3e2653c6-08dd-4481-8023-d360357a2a1d&accessTicket=10ca8e04-7262-495c-b1c0-7baee1e7c32a&dd_orientation=auto"
        viewModel.initAppData(this) {
            Timber.i("initData type: ${viewModel.type} url: ${viewModel.url}")
            this.runOnUiThread {
                binding.webView.loadUrl(viewModel.url)
            }
        }
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun init() {
        //
        viewModel.initData()
    }

    private fun checkPermissions(request: Boolean = false): Boolean {
        return true
//        var hasPermission = true
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            for (i in 0 until PERMISSIONS.size) {
//                val permission = PERMISSIONS[i]
//                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                    hasPermission = false
//                }
//            }
//            if (!hasPermission && request) {
//                //
//                this.requestPermissions(PERMISSIONS.toTypedArray(), REQUEST_CODE)
//            }
//        }
//        return hasPermission
    }

    private fun hookWebView() {
        val sdkInt = Build.VERSION.SDK_INT
        try {
            val factoryClass = Class.forName("android.webkit.WebViewFactory")
            val field: Field = factoryClass.getDeclaredField("sProviderInstance")
            field.setAccessible(true)
            var sProviderInstance = field.get(null)
            if (sProviderInstance != null) {
                Log.i("TAG", "sProviderInstance isn't null")
                return
            }
            val getProviderClassMethod: Method
            getProviderClassMethod = if (sdkInt > 22) {
                factoryClass.getDeclaredMethod("getProviderClass")
            } else if (sdkInt == 22) {
                factoryClass.getDeclaredMethod("getFactoryClass")
            } else {
                Log.i("TAG", "Don't need to Hook WebView")
                return
            }
            getProviderClassMethod.setAccessible(true)
            val factoryProviderClass = getProviderClassMethod.invoke(factoryClass) as Class<*>
            val delegateClass = Class.forName("android.webkit.WebViewDelegate")
            val delegateConstructor: Constructor<*> = delegateClass.getDeclaredConstructor()
            delegateConstructor.setAccessible(true)
            if (sdkInt < 26) { //低于Android O版本
                val providerConstructor: Constructor<*>? = factoryProviderClass.getConstructor(delegateClass)
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true)
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance())
                }
            } else {
                val chromiumMethodName: Field = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD")
                chromiumMethodName.setAccessible(true)
                var chromiumMethodNameStr = chromiumMethodName.get(null) as String?
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create"
                }
                val staticFactory: Method? = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass)
                if (staticFactory != null) {
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance())
                }
            }
            if (sProviderInstance != null) {
                field.set("sProviderInstance", sProviderInstance)
                Log.i("TAG", "Hook success!")
            } else {
                Log.i("TAG", "Hook failed!")
            }
        } catch (e: Throwable) {
            Log.w("TAG", e)
        }
    }
}
