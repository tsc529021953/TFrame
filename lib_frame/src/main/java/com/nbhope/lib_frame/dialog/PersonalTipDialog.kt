package com.nbhope.lib_frame.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.nbhope.lib_frame.R
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.databinding.PersonalLayoutTipBinding
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.nbhope.lib_frame.widget.BaseDialogFragment
import timber.log.Timber

/**
 * @author  tsc
 * @date  2023/2/19 10:56
 * @version 0.0.0-1
 * @description
 */
class PersonalTipDialog: BaseDialogFragment<PersonalLayoutTipBinding, BaseViewModel>() {

    override fun linkViewModel() {

    }

    var callBack : (() -> Unit)? = null

    var msgCallBack : ((msg: String?) -> String?)? = null

    var dismissCallBack: (() -> Unit)? = null

    override fun initView() {
        tipType = arguments?.getInt(TIP_TYPE)
        tipContent = arguments?.getString(TIP_CONTENT)
        tipTip = arguments?.getString(TIP_TIP)
        titleContent = arguments?.getString(TITLE_CONTENT)
        when(tipType) {
            UN_BIND_TIP -> {
                if (titleContent != null)
                    binding.tipTitleTv.text = titleContent
                binding.sureBtn.setOnClickListener {
                    callBack?.invoke()
                    hideDialog()
                }
                binding.cancelBtn.setOnClickListener {
                    hideDialog()
                }
            }
            ACTIVATE_BQ_TIP -> {
                binding.sureBtn.visibility = View.GONE
                binding.sureBtn2.visibility = View.VISIBLE
                binding.cancelBtn.visibility = View.GONE
                binding.sureBtn2.setOnClickListener {
                    callBack?.invoke()
                    hideDialog()
                }
            }
            RENEW_BQ_TIP -> {
                binding.sureBtn.setOnClickListener {
                    Timber.i("KTAG 续费")
                    // 打开新的界面
                    callBack?.invoke()
                    hideDialog()
                }
                binding.cancelBtn.setOnClickListener {
                    hideDialog()
                }
            }
            INPUT_TIP -> {
                binding.tipContentTv.visibility = View.GONE
                binding.textEt.visibility = View.VISIBLE
                if (titleContent != null)
                    binding.tipTitleTv.text = titleContent
                if (tipContent != null) {
                    binding.textEt.setText(tipContent!!)
                    binding.textEt.setSelection(tipContent!!.length)
                }
                binding.sureBtn.setOnClickListener {
                    val res = msgCallBack?.invoke(binding.textEt.text.toString())
                    if (res == null) hideDialog()
                    else ToastUtil.showS(res)
                }
                binding.cancelBtn.setOnClickListener {
                    hideDialog()
                }
            }
        }
        binding.tipContentTv.text = tipContent
        if (tipTip != null) {
            binding.tipTipTv.visibility = View.VISIBLE
            binding.tipTipTv.text = tipTip
        }
    }

    override val layoutId: Int = R.layout.personal_layout_tip

    var titleContent: String? = null
    var tipType: Int? = null
    var tipContent: String? = null
    var tipTip: String? = null

    companion object {
        const val UN_BIND_TIP = 0
        const val ACTIVATE_BQ_TIP = 1
        const val RENEW_BQ_TIP = 2
        const val MESSAGE_TIP = 3
        const val INPUT_TIP = 4

        const val TITLE_CONTENT = "TITLE_CONTENT"
        const val TIP_TYPE = "TIP_TYPE"
        const val TIP_CONTENT = "TIP_CONTENT"
        const val TIP_TIP = "TIP_TIP"

//        fun activateBQDialog(context: Context): PersonalTipDialog {
//            val fragment = newInstance(ACTIVATE_BQ_TIP
//                , context.resources.getString(R.string.ps_context_jh1) +
//                        if (HopeConstants.isSmallScreen()) LoginManagerImpl.SMALL_FREE_YEAR.toString()
//                        else LoginManagerImpl.BIG_FREE_YEAR.toString()
//                        + context.resources.getString(R.string.ps_context_jh2))
//            return fragment
//        }

//        fun activateFailBQDialog(context: Context, code: Int): PersonalTipDialog {
//            val fragment = newInstance(ACTIVATE_BQ_TIP
//                , context.resources.getString(R.string.ps_context_jh_fail) + " code: " + code)
//            return fragment
//        }

        fun messageDialog(message: String, fm: FragmentManager, tag: String = "personalTipDialog", callBack : (() -> Unit)? = null) {
            val fragment = newInstance(ACTIVATE_BQ_TIP, message)
            fragment.callBack = callBack
            messageDialog(message, callBack).show(
                fm,
                tag
            )
        }

        fun messageDialog(message: String, callBack : (() -> Unit)? = null): PersonalTipDialog {
            val fragment = newInstance(ACTIVATE_BQ_TIP, message)
            fragment.callBack = callBack
            return fragment
        }

        fun makeSureDialog(message: String, callBack : (() -> Unit)?): PersonalTipDialog {
            val fragment = newInstance(UN_BIND_TIP, message)
            fragment.callBack = callBack
            return fragment
        }

//        fun renewHopeDialog(context: Context, callBack : (() -> Unit)?): PersonalTipDialog {
//            val fragment = newInstance(RENEW_BQ_TIP, context.resources.getString(R.string.ps_context_kt), context.resources.getString(R.string.ps_context_tip))
//            fragment.callBack = callBack
//            return fragment
//        }

//        fun unbindDialog(context: Context, callBack : (() -> Unit)?): PersonalTipDialog {
//            val fragment = newInstance(UN_BIND_TIP, context.resources.getString(R.string.ps_context_unbind))
//            fragment.callBack = callBack
//            return fragment
//        }
//
//        fun unbindDialogCommon(title: String?, content: String, callBack : (() -> Unit)?): PersonalTipDialog {
//            val fragment = newInstance(UN_BIND_TIP, content, titleContent = title)
//            fragment.callBack = callBack
//            return fragment
//        }

        fun inputNameDialog(title: String?, content: String? = null, callBack : ((msg: String?) -> String?)?, dismissCallBack: (() -> Unit)?): PersonalTipDialog {
            val fragment = newInstance(INPUT_TIP, content, titleContent = title)
            fragment.msgCallBack = callBack
            fragment.dismissCallBack = dismissCallBack
            return fragment
        }

        fun newInstance(type: Int, content: String? = null, tip: String? = null, titleContent: String? = null): PersonalTipDialog {
            val fragment = PersonalTipDialog()
            val args = Bundle()
            args.putInt(TIP_TYPE, type)
            args.putString(TIP_CONTENT, content)
            args.putString(TIP_TIP, tip)
            args.putString(TITLE_CONTENT, titleContent)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        initWindowsView(361f, 276f)
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
