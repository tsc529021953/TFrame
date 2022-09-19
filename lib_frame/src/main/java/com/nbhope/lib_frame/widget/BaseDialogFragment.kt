package com.nbhope.lib_frame.widget

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.nbhope.lib_frame.R
import com.nbhope.lib_frame.utils.HopeUtils
import timber.log.Timber

abstract class BaseDialogFragment<T : ViewDataBinding, VM : ViewModel> : DialogFragment() {
    var dialogLocal: Dialog? = null
    protected lateinit var viewModel: VM
    protected lateinit var binding: T
    private var rootView: View? = null
    private val TAG = "BaseDialogFragment"
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        linkViewModel()
        rootView = binding.root

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.BaseDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogLocal = super.onCreateDialog(savedInstanceState)
        //点击弹出框外面是否可取消
        dialogLocal!!.setCanceledOnTouchOutside(true)
        //点击返回键是否可取消
        dialogLocal!!.setCancelable(true)
        return dialogLocal as Dialog
    }

    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onStart() {
        super.onStart()
        //设置dialogFragment位置及属性
        if (null != dialogLocal) {
            val window = dialogLocal!!.window
            if (null != window) {
                val lp = window.attributes
                lp.gravity = Gravity.CENTER
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                window.attributes = lp

                initWindows(window, lp)
            }
        }
    }

    fun initWindowsView(width: Any, height: Any, anim: Int = 0) {
        if (null != dialogLocal) {
            val window = dialogLocal!!.window
            if (null != window) {
                val lp = window.attributes
                lp.gravity = Gravity.CENTER
                if (width is Float && height is Float) {
                    lp.width = HopeUtils.dip2px(activity!!, width)
                    lp.height = HopeUtils.dip2px(activity!!, height)
                } else if (width is Float && height is Int) {
                    lp.width = HopeUtils.dip2px(activity!!, width)
                    when (height) {
                        WindowManager.LayoutParams.WRAP_CONTENT -> {
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                        }
                        WindowManager.LayoutParams.MATCH_PARENT -> {
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT
                        }
                        else -> {
                            lp.height = HopeUtils.dip2px(activity!!, height.toFloat())
                        }
                    }
                } else if (width is Int && height is Float) {
                    lp.height = HopeUtils.dip2px(activity!!, height)
                    when (width) {
                        WindowManager.LayoutParams.WRAP_CONTENT -> {
                            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                        }
                        WindowManager.LayoutParams.MATCH_PARENT -> {
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT
                        }
                        else -> {
                            lp.width = HopeUtils.dip2px(activity!!, width.toFloat())
                        }
                    }
                } else if (width is Int && height is Int) {
                    lp.width = width
                    lp.height = height
                }
                initWindows(window, lp)
                window?.setWindowAnimations(anim)
            }

        }
    }

    private fun initWindows(window: Window, lp: WindowManager.LayoutParams) {
        window.attributes = lp
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.decorView.setOnSystemUiVisibilityChangeListener {
            var uiOptions: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or  //布局位于状态栏下方
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or  //全屏
                    View.SYSTEM_UI_FLAG_FULLSCREEN or  //隐藏导航栏
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            uiOptions = uiOptions or 0x00001000
            window.decorView.systemUiVisibility = uiOptions
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideSystemUI()
    }

    /**
     * 初始化model
     *
     * @return int
     */
    protected abstract fun linkViewModel()
    protected abstract fun initView()

    /**
     * 绑定布局
     *
     * @return int
     */
    protected abstract val layoutId: Int

    //2、复写show方法，在dialogshow出来后写上fm.executePendingTransactions();
    override fun show(
            fm: FragmentManager,
            tag: String?
    ) {
        val ft = fm.beginTransaction()
        ft.add(this, tag)
        // 这里把原来的commit()方法换成了commitAllowingStateLoss()
        // 解决Can not perform this action after onSaveInstanceState with DialogFragment
        ft.commitAllowingStateLoss()
        //解决java.lang.IllegalStateException: Fragment already added
        fm.executePendingTransactions()
    }

    override fun dismiss() {
        if (dialogLocal != null) {
            dialogLocal!!.dismiss()
        }
    }

    /**
     * 关闭弹出框
     */
    protected fun hideDialog() {
        try {
            //没初始化就会出现问题
            if (dialogLocal != null && dialogLocal!!.isShowing) {
                dialogLocal!!.dismiss()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

//    /**
//     * 短时间显示Toast
//     *
//     * @param msg 消息体
//     */
//    protected fun toastShort(msg: String?) {
//        ToastUtils.show( msg);
//    }
//
//    /**
//     * 短时间显示Toast
//     *
//     * @param resId 消息体
//     */
//    protected fun toastShort(resId: Int) {
//        ToastUtils.show(resId);
//    }

    protected fun hideSystemUI() {
        Timber.e("I Can " + "hideSystemUI")
        activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    }
}