package com.xs.xs_by.dialog

import android.app.SearchManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableBoolean
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.widget.BaseDialogFragment
import com.xs.xs_by.R
import com.xs.xs_by.databinding.DialogTipByBinding

/**
 * @author  tsc
 * @date  2024/4/8 9:16
 * @version 0.0.0-1
 * @description
 */
class BYTipDialog constructor(
    var title: String? = "",
    open: Boolean = true,
    var message: String? = "",
    var cancelStr: String? = "",
    var sureStr: String? = "",
    var callBack: (() -> Boolean)? = null,
    var cancelCallBack: (() -> Boolean)? = null
) : BaseDialogFragment<DialogTipByBinding, BaseViewModel>() {

    companion object {

        private var mDialog: BYTipDialog? = null

        fun showInfoTip(
            activity: AppCompatActivity,
            open: Boolean = true,
            title: String? = "",
            message: String? = "",
            cancelStr: String? = "",
            sureStr: String? = "",
            callBack: (() -> Boolean)? = null,
            cancelCallBack: (() -> Boolean)? = null
        ) {
            if (mDialog?.isVisible == true) return
            BYTipDialog(title, open, message, cancelStr, sureStr, callBack, cancelCallBack).show(
                activity.supportFragmentManager,
                "TipDialog"
            )
        }
    }

    override val layoutId: Int = R.layout.dialog_tip_by

    private var openObs = ObservableBoolean(true)

    init {
        openObs.set(open)
    }

    override fun linkViewModel() {

    }

    override fun initView() {
        if (!title.isNullOrEmpty())
            binding.titleTv.text = title
        if (!message.isNullOrEmpty())
            binding.messageTv.text = message
        if (!cancelStr.isNullOrEmpty())
            binding.cancelBtn.text = cancelStr
        if (!sureStr.isNullOrEmpty())
            binding.sureBtn.text = sureStr

        binding.cancelBtn.setOnClickListener {
            if (cancelCallBack == null || cancelCallBack?.invoke() == true) {
                this.dismiss()
            }
        }
        binding.sureBtn.setOnClickListener {
            if (callBack == null || callBack?.invoke() == true) {
                this.dismiss()
            }
        }
        this.dialog?.setCanceledOnTouchOutside(false)
        this.dialog?.setCancelable(false)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mDialog = null
    }

    override fun onStart() {
        super.onStart()
        initWindowsView(
            resources.getDimension(R.dimen.dialog_tip_by_width).toInt(),
            resources.getDimension(R.dimen.dialog_tip_by_height).toInt()
        )
    }


}
