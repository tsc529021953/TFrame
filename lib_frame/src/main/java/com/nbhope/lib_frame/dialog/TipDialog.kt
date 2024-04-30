package com.nbhope.lib_frame.dialog

import androidx.appcompat.app.AppCompatActivity
import com.nbhope.lib_frame.R
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.databinding.DialogTipBinding
import com.nbhope.lib_frame.widget.BaseDialogFragment

/**
 * @author  tsc
 * @date  2024/4/8 9:16
 * @version 0.0.0-1
 * @description
 */
class TipDialog constructor(var title: String? = "",var message: String? = "",var cancelStr: String? = "",var sureStr: String? = ""
                            ,var callBack : (() -> Boolean)? = null,var cancelCallBack : (() -> Boolean)? = null): BaseDialogFragment<DialogTipBinding, BaseViewModel>() {

    companion object{

        private var mDialog: TipDialog? = null

        fun showInfoTip(activity: AppCompatActivity, title: String? = "", message: String? = "", cancelStr: String? = "", sureStr: String? = ""
                        , callBack : (() -> Boolean)? = null, cancelCallBack : (() -> Boolean)? = null) {
            if (mDialog?.isVisible == true) return
            TipDialog(title, message, cancelStr, sureStr, callBack, cancelCallBack).show(
                activity.supportFragmentManager,
                "TipDialog"
            )
        }
    }

    override fun linkViewModel() {

    }

    override fun initView() {
        if (!title.isNullOrEmpty())
            binding.titleTv.text = title
        if (!message.isNullOrEmpty())
            binding.messageTv.text = message
        if (!cancelStr.isNullOrEmpty())
            binding.closeBtn.text = cancelStr
        if (!sureStr.isNullOrEmpty())
            binding.sureBtn.text = sureStr

        binding.closeBtn.setOnClickListener {
            if (cancelCallBack == null || cancelCallBack?.invoke() == true) {
                this.dismiss()
            }
        }
        binding.sureBtn.setOnClickListener {
            if (callBack == null || callBack?.invoke() == true) {
                this.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDialog = null
    }

    override fun onStart() {
        super.onStart()
        initWindowsView(
            resources.getDimension(R.dimen.float_dialog_tip_width).toInt(),
            resources.getDimension(R.dimen.float_dialog_tip_height).toInt()
        )
    }

    override val layoutId: Int = R.layout.dialog_tip
}
