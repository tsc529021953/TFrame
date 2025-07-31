package com.sc.tmp_translate

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.media.AudioManager
import android.view.Display
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.DisplayUtil
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.view.DrawLayoutUtils
import com.sc.tmp_translate.base.BaseTransActivity
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.databinding.ActivityTranslateBinding
import com.sc.tmp_translate.dialog.DisplayDialog
import com.sc.tmp_translate.dialog.TextPlayDialog
import com.sc.tmp_translate.dialog.TextSizeDialog
import com.sc.tmp_translate.service.TmpServiceDelegate
import com.sc.tmp_translate.view.TransMoreDisplay
import com.sc.tmp_translate.vm.TranslateViewModel
import com.sc.tmp_translate.weight.KeepStateNavigator
import timber.log.Timber
import javax.inject.Inject


/**
 * 1.侧边设置界面
 * 1）同显/异显 主机侧一个界面 异显一个界面   异显一套界面
 * 2）字体设置 动态改变 *
 * 3）文本播放设置 *
 * 4）翻译记录
 * 对话组件实现
 * 同显
 * 左侧 上外文 下中文   右侧  上中文  下外文   最上面显示时间
 * 异显
 * 主屏都显示中文   副屏都显示外文    上面显示时间
 * 对话组件实现
 * 异显实现 两侧语种同步,文字大小同步
 * 下拉的颜色 转换的图标换下 方向下或者改颜色
 *  数据的位置问题
 *  异显实现  数据更新 自动翻到最新 异显语种翻译 异显检测（按钮那边禁用并提示）
 *  动态识别mic信息，
 *  开始和暂停按钮  实现双喇叭录音  接入录音翻译
 *  多种开始暂停控制
 *  翻译记录的编号问题
 *   录音文件记录 (记录 + 列表 + 播放 + 删除 删除本地文件同步  + 加载本地记录)
 *   异显的播放按钮
 *   进入退出算一次交流，所以需要加一个编号，用来判断是第几次
 * TODO tts  + 区分是否说话，加静音识别  +  异显界面的刷新 +   翻译完把音频文件删除 + 音量按键 + 主翻译的屏蔽打开
 */
class TranslateActivity : BaseTransActivity<ActivityTranslateBinding, TranslateViewModel>() {

    companion object {

        private var PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

    }

    @Inject
    override lateinit var viewModel: TranslateViewModel

    override var layoutId: Int = R.layout.activity_translate

    /*界面导航*/
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private var currLayout: View? = null

    private var dialog: TransMoreDisplay? = null
    private var display: Display? = null

    private var displayObsListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            runOnUiThread {
                refreshDisplay()
            }
        }
    }

    private var languageObsListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            runOnUiThread {
                refreshLanguage()
            }
        }
    }

    private var translatingObsListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            runOnUiThread {
                if (TmpServiceDelegate.getInstance().getTranslatingObs()?.get() == true) {
                    dialog?.refreshData()
                }
            }
        }
    }

    override fun subscribeUi() {
        checkPermissions()
        firstView = true
        initView()
        initNav()
    }

    override fun initData() {
        val devices =
            (getSystemService(Context.AUDIO_SERVICE) as AudioManager).getDevices(AudioManager.GET_DEVICES_INPUTS)
        log("initData devices size ${devices.size}")
    }

    override fun linkViewModel() {
        binding.vm = viewModel
        initListener()
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        navController.backStack.forEach {
//            System.out.println("onBackPressed Destination: ${it.destination.id}")
//        }
//        System.out.println("onBackPressed ${navController.currentDestination?.id} ${R.id.navigation_trans_main} ${R.id.navigation_translating} ${R.id.navigation_trans_record}")
//        when (navController.currentDestination?.id) {
//            R.id.navigation_trans_record -> back()
//            R.id.navigation_translating -> {
//                back()
//                TmpServiceDelegate.getInstance().setTranslating(false)
//            }
//
//            else -> finish()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("释放")
        TmpServiceDelegate.getInstance().getMoreDisplayObs()?.removeOnPropertyChangedCallback(displayObsListener)
        TmpServiceDelegate.getInstance().getTransLangObs()?.removeOnPropertyChangedCallback(languageObsListener)
        TmpServiceDelegate.getInstance().getTranslatingObs()?.removeOnPropertyChangedCallback(translatingObsListener)
        if (dialog != null) {
            try {
                dialog?.dismiss()
            } catch (e: Exception) {
            }
        }
    }

    private fun initListener() {
        if (TmpServiceDelegate.getInstance().getMoreDisplayObs() != null) {
            binding.tmp = TmpServiceDelegate.getInstance()
            TmpServiceDelegate.getInstance().getMoreDisplayObs()?.addOnPropertyChangedCallback(displayObsListener)
            TmpServiceDelegate.getInstance().getTransLangObs()?.addOnPropertyChangedCallback(languageObsListener)
            TmpServiceDelegate.getInstance().getTranslatingObs()?.addOnPropertyChangedCallback(translatingObsListener)
            refreshLanguage()
            refreshDisplay()
        }
    }

    private fun initView() {
        // logo
        binding.logoLy.visibility = View.GONE
        binding.logoLy.setOnClickListener {
            binding.logoLy.visibility = View.GONE
        }
        // 右侧
        DrawLayoutUtils.initDL(binding.dlLy)
        binding.showDlBtn.setOnClickListener {
            DrawLayoutUtils.openDL(binding.dlLy)
        }
        binding.displayBtn.setOnClickListener {
            DisplayDialog.newInstance().show(supportFragmentManager, "DisplayDialog")
            DrawLayoutUtils.closeDL(binding.dlLy)
        }
        binding.fontSizeBtn.setOnClickListener {
            TextSizeDialog.newInstance().show(supportFragmentManager, "TextSizeDialog")
            DrawLayoutUtils.closeDL(binding.dlLy)
        }
        binding.textBtn.setOnClickListener {
            TextPlayDialog.newInstance().show(supportFragmentManager, "TextPlayDialog")
            DrawLayoutUtils.closeDL(binding.dlLy)
        }
        binding.recordBtn.setOnClickListener {
            layoutClick(binding.recordBtn, true) {
                this.runOnUiThread {
                    showDLBtn(false)
                    navController.navigate(R.id.navigation_trans_record)
                }
            }
        }
    }

    private fun initNav() {
        mNavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navigator = KeepStateNavigator(this, mNavHostFragment.childFragmentManager, R.id.nav_host_fragment)
        navController = mNavHostFragment.navController
        navController.navigatorProvider.addNavigator(navigator)
        navController.setGraph(R.navigation.home_navigation)
//        navController.navigate(R.id.navigation_trans_main)
        navController.addOnDestinationChangedListener { navController, navDestination, bundle ->
            log("页面切换 $navDestination")
        }
    }

    fun refreshMoreDisplay() {

    }

    private fun showDLBtn(show: Boolean = true) {
        binding.showDlBtn.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun layoutClick(view: View, hideDL: Boolean = false, callback: (() -> Unit)? = null) {
        if (currLayout == view) return
        if (hideDL) {
            DrawLayoutUtils.closeDL(binding.dlLy)
        }
        callback?.invoke()
    }

    fun refreshDisplay() {
        val res = TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() ?: false
        val displays = DisplayUtil.getDisplays(this)
        if (displays.isNullOrEmpty()) return
        val eq = display == displays[0]
        if (dialog == null || !eq) {
            if (!eq) {
                dialog?.hide()
                dialog?.dismiss()
            }
            dialog = TransMoreDisplay(this, displays[0]) { v, id ->
                setFontSize(v, id)
            }
            dialog?.refreshTmp()
        }
        if (res) {
            dialog?.showFun()
        } else if (!res) {
            dialog?.hideFun()
        }
    }

    fun refreshLanguage() {
        dialog?.refreshLanguage()
    }

    private fun back() {
        this.runOnUiThread {
            showDLBtn()
//            if (!navController.popBackStack(R.id.navigation_trans_main, false)) {
//                navController.navigate(R.id.navigation_trans_main);
//            }
//            navController.navigate(R.id.navigation_trans_main)
//                    val res = navController.popBackStack()
            navController?.navigateUp()
            log("执行返回3")
        }
    }

    private fun checkPermissions() {
        if (!hasPermission()) {
            requestPermissions(PERMISSIONS, 10086)
        }
    }

    private fun hasPermission(): Boolean {
        var has = true
        PERMISSIONS.forEach {
            if (checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                has = false
            }
        }
        Timber.i("hasPermission $has")
        return has
    }

    private fun log(msg: Any?) {
        System.out.println("Trans：${msg ?: "null"}")
    }

    override fun onFontSizeChanged(fontSize: Float) {
        dialog?.onFontSizeChanged()
    }

    override fun onLiveEB(cmd: String, data: String) {
        when (cmd) {
            MessageConstant.CMD_BACK -> {
                back()
            }

            MessageConstant.CMD_TRANSLATING -> {
                log("翻译 $data")
                val translating = data.toBoolean()
                if (translating) {
                    this.runOnUiThread {
                        navController.navigate(R.id.navigation_translating)
                    }
                } else back()
//                else if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() == true) {
//                    this.runOnUiThread {
//                        if (!navController.popBackStack(R.id.navigation_trans_record, false)) {
//                            navController.navigate(R.id.navigation_trans_record);
//                        }
////                        navController.navigate(R.id.navigation_trans_record)
//                    }
//                    TmpServiceDelegate.getInstance().setTransRecord(false)
//                } else back()
                TmpServiceDelegate.getInstance().setTranslating(translating)
            }

            MessageConstant.CMD_BIND_SUCCESS -> {
                initListener()
            }
        }
    }
}
