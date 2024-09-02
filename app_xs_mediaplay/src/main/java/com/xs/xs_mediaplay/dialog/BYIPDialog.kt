package com.xs.xs_mediaplay.dialog

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableBoolean
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.StringUtil
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.nbhope.lib_frame.widget.BaseDialogFragment
import com.xs.xs_mediaplay.R
import com.xs.xs_mediaplay.databinding.DialogChangeIpBinding

/**
 * @author  tsc
 * @date  2024/4/8 9:16
 * @version 0.0.0-1
 * @description
 */
class BYIPDialog constructor(
    var ip: String,
    var port: Int,
    var ip2: String,
    var port2: Int,
    var cancelStr: String? = "",
    var sureStr: String? = "",
    var callBack: ((ip: String, port: Int, ip2: String, port2: Int) -> Boolean)? = null,
    var cancelCallBack: (() -> Boolean)? = null
) : BaseDialogFragment<DialogChangeIpBinding, BaseViewModel>() {

    companion object {

        private var mDialog: BYIPDialog? = null

        fun showInfoTip(
            activity: AppCompatActivity,
            ip: String,
            port: Int,
            ip2: String,
            port2: Int,
            cancelStr: String? = "",
            sureStr: String? = "",
            callBack: ((ip: String, port: Int, ip2: String, port2: Int) -> Boolean)? = null,
            cancelCallBack: (() -> Boolean)? = null
        ) {
            if (mDialog?.isVisible == true) return
            BYIPDialog(ip, port, ip2, port2, cancelStr, sureStr, callBack, cancelCallBack).show(
                activity.supportFragmentManager,
                "TipDialog"
            )
        }
    }

    override val layoutId: Int = R.layout.dialog_change_ip

    private var openObs = ObservableBoolean(true)

    init {
//        openObs.set(open)
    }

    override fun linkViewModel() {

    }

    override fun initView() {
        binding.ipEt.setText(ip)
        binding.portEt.setText(port.toString())
        binding.ip2Et.setText(ip2)
        binding.port2Et.setText(port2.toString())
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
            val ip = binding.ipEt.text.toString().trim()
            val port = binding.portEt.text.toString().trim().toIntOrNull()
            val ip2 = binding.ip2Et.text.toString().trim()
            val port2 = binding.port2Et.text.toString().trim().toIntOrNull()
            if (!StringUtil.isIP(ip) || !StringUtil.isIP(ip2)) {
                ToastUtil.showS("请输入正确格式的ip")
                return@setOnClickListener
            }
            if (port == null || port!! > 65000 || port2 == null || port2!! > 65000) {
                ToastUtil.showS("请输入正确格式的端口号")
                return@setOnClickListener
            }
            if (callBack == null || callBack?.invoke(ip, port, ip2, port2) == true) {
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
            resources.getDimension(R.dimen.dialog_tip_by_width).toInt(),
            resources.getDimension(R.dimen.dialog_tip_by_ip_height).toInt()
        )
    }


}
