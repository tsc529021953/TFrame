package com.nbhope.lib_frame.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nbhope.lib_frame.R
import timber.log.Timber

/**
 * @author  tsc
 * @date  2024/4/8 9:53
 * @version 0.0.0-1
 * @description
 */
class TipNFDialog(context: Context, var title: String? = "",var message: String? = "",var cancelStr: String? = "",var sureStr: String? = ""
                  ,var callBack : (() -> Boolean)? = null,var cancelCallBack : (() -> Boolean)? = null, var isFloat: Boolean = true) : Dialog(context, R.style.MainWindowDialog) {

    private var mRootView: View? = null

    private var isShow = false
    private var addError = false

    companion object{

        private var mDialog: TipDialog? = null

        fun showInfoTip(context: Context, title: String? = "", message: String? = "", cancelStr: String? = "", sureStr: String? = ""
                        , callBack : (() -> Boolean)? = null, cancelCallBack : (() -> Boolean)? = null) {
            if (mDialog?.isVisible == true) return
            TipNFDialog(context, title, message, cancelStr, sureStr, callBack, cancelCallBack).show()
        }
    }

    init {
        init()
    }

    protected fun init() {
        val window = window
        window?.run {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or 0x00010000)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = 0x00010000
            val params = attributes
            when {
                Build.VERSION.SDK_INT >= 26 -> { //8.0新特性
                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                }
                Build.VERSION.SDK_INT <= 19 -> {
                    params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                }
                else -> {
                    params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                }
            }
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_tip_nf, null)
            setContentView(mRootView)
        }

        if (!title.isNullOrEmpty())
            findViewById<TextView>(R.id.title_tv).text = title
        if (!message.isNullOrEmpty())
            findViewById<TextView>(R.id.message_tv).text = message
        val cancelBtn = findViewById<TextView>(R.id.close_btn)
        if (!cancelStr.isNullOrEmpty())
            cancelBtn.text = cancelStr
        cancelBtn.setOnClickListener {
            if (cancelCallBack == null || cancelCallBack?.invoke() == true) {
                this.dismiss()
            }
        }

        val sureBtn = findViewById<TextView>(R.id.sure_btn)
        if (!sureStr.isNullOrEmpty())
            sureBtn.text = sureStr
        sureBtn.setOnClickListener {
            if (callBack == null || callBack?.invoke() == true) {
                this.dismiss()
            }
        }
    }

    override fun show() {
        Timber.e("BinderUICtr", "show is coming")
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams()
//        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isFloat) {
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams.width = context.resources.getDimension(R.dimen.float_dialog_tip_width).toInt()
        layoutParams.height =  context.resources.getDimension(R.dimen.float_dialog_tip_height).toInt()
        try {
            if (mRootView!!.parent != null) {
                (mRootView!!.parent as ViewGroup).removeView(mRootView)
            }
            windowManager.addView(mRootView, layoutParams)
            isShow = true
        } catch (e: Exception) {
            addError = true
            mRootView?.let { setContentView(it) }
            super.show()
        }
    }

    override fun isShowing(): Boolean {
        return if (addError) {
            super.isShowing()
        } else isShow
    }

    override fun dismiss() {

//        try {
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (addError) {
            super.dismiss()
            return
        }
        try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.removeView(mRootView)
            isShow = false
        } catch (e: Exception) {
            super.dismiss()
        }
        //        SpeechManager.INSTANCE.initTianmao();
    }

}
