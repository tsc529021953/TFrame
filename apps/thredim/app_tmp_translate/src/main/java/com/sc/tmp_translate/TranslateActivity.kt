package com.sc.tmp_translate

import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.view.DrawLayoutUtils
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.databinding.ActivityTranslateBinding
import com.sc.tmp_translate.dialog.TextSizeDialog
import com.sc.tmp_translate.vm.TranslateViewModel
import com.sc.tmp_translate.weight.KeepStateNavigator
import javax.inject.Inject


/**
 * 1.侧边设置界面
 * 1）同显/异显 主机侧一个界面 异显一个界面   异显一套界面
 * 2）字体设置 动态改变
 * 3）文本播放设置
 * 4）翻译记录
 */
class TranslateActivity: BaseBindingActivity<ActivityTranslateBinding, TranslateViewModel>() {


    @Inject
    override lateinit var viewModel: TranslateViewModel

    override var layoutId: Int = R.layout.activity_translate

    /*界面导航*/
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private var currLayout: View? = null

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_BACK -> {
                this.runOnUiThread {
                    showDLBtn()
                    navController.navigate(R.id.navigation_trans_main)
//                    val res = navController.popBackStack()
                    log("执行返回3")
                }
//                navController.navigateUp()
            }
            MessageConstant.CMD_TRANSLATING -> {
                log("翻译 ${it.data}")
                viewModel.translatingObs.set(it.data.toBoolean())
            }
        }
    }

    override fun subscribeUi() {
        initView()
        initNav()
    }

    override fun initData() {
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
    }
    
    override fun linkViewModel() {
        binding.vm = viewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        log("释放")
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
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

        }
        binding.fontSizeBtn.setOnClickListener {
            TextSizeDialog.newInstance().show(supportFragmentManager, "TextSizeDialog")
        }
        binding.textBtn.setOnClickListener {

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
        navController.navigate(R.id.navigation_trans_main)
        navController.addOnDestinationChangedListener { navController, navDestination, bundle ->
            log("页面切换 $navDestination")
        }
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

    private fun log(msg: Any?) {
        System.out.println("Trans：${msg ?: "null"}")
    }
}