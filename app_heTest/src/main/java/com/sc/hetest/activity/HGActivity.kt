package com.sc.hetest.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.phmusic.player.AudioFocusManager
import com.nbhope.phmusic.player.SilentPlayer
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HECommon
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityHornBinding
import com.sc.hetest.databinding.ActivityMainBinding
import com.sc.hetest.databinding.ActivityVerInfoBinding
import com.sc.hetest.vm.HornViewModel
import com.sc.hetest.vm.MainViewModel
import com.sc.hetest.vm.VerInfoViewModel
import javax.inject.Inject

/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.HG_PATH)
class HGActivity : BaseBindingActivity<ActivityHornBinding, HornViewModel>(){

    companion object{

    }

    override var layoutId: Int = R.layout.activity_horn
    override fun subscribeUi() {
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.HG_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.HG_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
    }

    override fun initData() {
        viewModel.initData()

    }

    @Inject
    override lateinit var viewModel: HornViewModel
    override fun linkViewModel() {
        binding.vm = viewModel

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}