package com.sc.tmp_translate.dialog

import android.os.Bundle
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.utils.ViewUtil
import com.nbhope.lib_frame.widget.BaseDialogFragment
import com.sc.tmp_translate.R
import com.sc.tmp_translate.base.BaseTransDialogFragment
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.databinding.DialogTextSizeBinding
import com.sc.tmp_translate.service.TmpServiceDelegate

/**
 * TODO 选中状态
 */
class TextSizeDialog : BaseTransDialogFragment<DialogTextSizeBinding, BaseViewModel>() {

    companion object{

        fun newInstance(): TextSizeDialog {
            val fragment = TextSizeDialog()
            val args = Bundle()
//            args.putInt(TIP_TYPE, type)
//            args.putString(TIP_CONTENT, content)
            fragment.arguments = args
            return fragment
        }
        
    }

    override val layoutId: Int = R.layout.dialog_text_size

    override fun linkViewModel() {

    }

    override fun initView() {
        binding.tmp = TmpServiceDelegate.getInstance()
    }

    override fun onFontSizeChanged(fontSize: Float) {
        setFontSize(binding.titleTv, R.dimen.main_title_size)
        setFontSize(binding.minTv, R.dimen.main_text_size)
        setFontSize(binding.norTv, R.dimen.main_text_size)
        setFontSize(binding.maxTv, R.dimen.main_text_size)
    }

    override fun onLiveEB(cmd: String, data: String) {

    }

    override fun onStart() {
        super.onStart()
        initWindowsView(ViewUtil.getOrigin(resources, R.dimen.dialog_text_size_width), ViewUtil.getOrigin(resources, R.dimen.dialog_text_size_height))
    }

}