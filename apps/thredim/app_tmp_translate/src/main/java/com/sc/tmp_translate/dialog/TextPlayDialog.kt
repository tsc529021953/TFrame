package com.sc.tmp_translate.dialog

import android.os.Bundle
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.ViewUtil
import com.nbhope.lib_frame.widget.BaseDialogFragment
import com.sc.tmp_translate.R
import com.sc.tmp_translate.base.BaseTransDialogFragment
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.databinding.DialogDisplayBinding
import com.sc.tmp_translate.databinding.DialogTextPlayBinding
import com.sc.tmp_translate.databinding.DialogTextSizeBinding
import com.sc.tmp_translate.service.TmpServiceDelegate

/**
 *
 */
class TextPlayDialog : BaseTransDialogFragment<DialogTextPlayBinding, BaseViewModel>() {

    companion object{

        fun newInstance(): TextPlayDialog {
            val fragment = TextPlayDialog()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

    }

    override val layoutId: Int = R.layout.dialog_text_play

    override fun linkViewModel() {

    }

    override fun initView() {
        binding.tmp = TmpServiceDelegate.getInstance()
    }

    override fun onFontSizeChanged(fontSize: Float) {
        setFontSize(binding.titleTv, R.dimen.main_title_size)
        setFontSize(binding.minTv, R.dimen.main_text_size)
        setFontSize(binding.norTv, R.dimen.main_text_size)
    }

    override fun onLiveEB(cmd: String, data: String) {

    }

    override fun onStart() {
        super.onStart()
        initWindowsView(ViewUtil.getOrigin(resources, R.dimen.dialog_text_size_width), ViewUtil.getOrigin(resources, R.dimen.dialog_text_size_height))
    }

}
