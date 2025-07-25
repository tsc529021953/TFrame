package com.sc.tmp_translate.view

import com.jeremyliao.liveeventbus.LiveEventBus
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.ValueHolder
import com.sc.tmp_translate.R
import com.sc.tmp_translate.base.BaseTransFragment
import com.sc.tmp_translate.constant.MessageConstant.CMD_BACK
import com.sc.tmp_translate.databinding.FragmentTransRecordBinding
import com.sc.tmp_translate.vm.TransRecordViewModel
import javax.inject.Inject

class TransRecordFragment : BaseTransFragment<FragmentTransRecordBinding, TransRecordViewModel>() {

    @Inject
    override lateinit var viewModel: TransRecordViewModel

    override var layoutId: Int = R.layout.fragment_trans_record

    override fun onFontSizeChanged(fontSize: Float) {
        setFontSize(binding.titleTv, R.dimen.main_title_size)
    }

    override fun onLiveEB(cmd: String, data: String) {
        
    }

    override fun linkViewModel() {
        
    }

    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            ValueHolder.setValue {
                LiveEBUtil.post(RemoteMessageEvent(CMD_BACK, ""))
            }
        }
    }

    override fun initData() {
        
    }
}