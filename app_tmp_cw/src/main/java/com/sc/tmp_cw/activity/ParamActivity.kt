package com.sc.tmp_cw.activity

import android.widget.SeekBar
import com.alibaba.android.arouter.facade.annotation.Route
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.dialog.TipDialog
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.phfame.utils.VoiceUtil
import com.sc.tmp_cw.R
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivityParamBinding
import com.sc.tmp_cw.vm.ParamViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/10 15:00
 * @version 0.0.0-1
 * @description
 */
@Route(path = MessageConstant.ROUTH_PARAM)
class ParamActivity : BaseBindingActivity<ActivityParamBinding, ParamViewModel>() {

    override var layoutId: Int = R.layout.activity_param

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_URGENT_NOTICE -> {
//                finish()
            }
        }
    }

    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.volumeSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                VoiceUtil.setVolume(this@ParamActivity, seekBar!!.progress)
            }
        })
        binding.speedSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override fun initData() {
        binding.vm = viewModel
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
        viewModel.volumeObs.set(VoiceUtil.getVolume(this))
        viewModel.initData()
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
    }

    @Inject
    override lateinit var viewModel: ParamViewModel

    override fun finish() {
        // 弹出框
        TipDialog.showInfoTip(this, getString(R.string.title_tip), getString(R.string.setting_is_save_or_cancel)
            , getString(R.string.text_no), getString(R.string.text_yes), {
                // 保存
                val speed = binding.speedSb.progress / 10.0f
                System.out.println("speed???:$speed")
                viewModel.spManager.setFloat(MessageConstant.SP_MARQUEE_SPEED, speed)

                super.finish()
                return@showInfoTip true
            }, {
                super.finish()
                return@showInfoTip true
            })
    }
}
