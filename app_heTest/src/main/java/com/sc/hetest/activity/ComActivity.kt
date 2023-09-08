package com.sc.hetest.activity

import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.common.BasePath
import com.sc.hetest.R
import com.sc.hetest.bean.InfoItem
import com.sc.hetest.common.HEPath
import com.sc.hetest.databinding.ActivityComBinding
import com.sc.hetest.vm.ComViewModel
import com.sc.lib_system.sp.SerialPortUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * author: sc
 * date: 2022/12/10
 */
@Route(path = HEPath.SERIAL_PATH)
class ComActivity : BaseBindingActivity<ActivityComBinding, ComViewModel>(){

    override var layoutId: Int = R.layout.activity_com

    var nameAdapter: ArrayAdapter<String>? = null

    var baudAdapter: ArrayAdapter<String>? = null

    var watcher: TextWatcher? = null

    override fun subscribeUi() {
//        watcher = CustomTextWatcher(binding.inputEt)
        watcher = object : TextWatcher {
            private var flag = true
            private var index = 0
            private var beforeLen = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (flag) {
                    beforeLen = s.length
                    index = start - count + 1
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                flag = false
                val hex: String = SerialPortUtil.toHex(s.toString())
                if (hex == s.toString()) {
                    flag = true
                    if (beforeLen < hex.length) {
                        index = index + hex.length - beforeLen - 1
                    }
                    if (index < 0) index = 0
                    if (index > hex.length) index = hex.length
                    binding.inputEt.setSelection(index)
                    return
                }
                binding.inputEt.setText(hex)
            }
        }
        binding.failTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.SERIAL_PATH, InfoItem.STATE_FAIL)
            finish()
        }
        binding.successTv.setOnClickListener {
            SPUtils.getInstance().put(HEPath.SERIAL_PATH, InfoItem.STATE_SUCCESS)
            finish()
        }
        binding.touchSetting.setOnClickListener {
            if (binding.btnLy.visibility == View.VISIBLE) {
                binding.ctrlLy.visibility = View.VISIBLE
                binding.btnLy.visibility = View.GONE
            }
            else {
                binding.btnLy.visibility = View.VISIBLE
                binding.ctrlLy.visibility = View.GONE
            }
        }
        binding.btnLy.visibility = View.GONE
        initNameAdapter()
        initBaudAdapter()
        binding.clearTv.setOnClickListener {
            binding.infoTv.text = ""
        }
        binding.hexTv.setOnClickListener {
            if (viewModel.hex.get() == "TEXT") {
                viewModel.hex.set("HEX")
                binding.inputEt.addTextChangedListener(watcher!!)
            } else {
                viewModel.hex.set("TEXT")
                binding.inputEt.removeTextChangedListener(watcher!!)
            }
        }
        binding.openTv.setOnClickListener {
            if (viewModel.open.get() == "打开") {
                val res = viewModel.spUtil.open()
                if (res) {
                    viewModel.open.set("关闭")
                    toast("打开串口 ${viewModel.spUtil.sp} 成功")
                }
                else toast("打开串口 ${viewModel.spUtil.sp} 失败")
            } else {
                viewModel.open.set("打开")
                viewModel.spUtil.dispose()
                toast("关闭串口 ${viewModel.spUtil.sp}")
            }
        }
        binding.sendTv.setOnClickListener {
            Timber.i("HETAG send ${ binding.inputEt.text}")
            var msg = binding.inputEt.text.trim().toString()
            if (viewModel.hex.get() != "TEXT") msg = msg.replace(" ", "")
            Timber.i("HETAG send $msg")
            if (viewModel.spUtil.isOpen) {
                val hex = checkHexString(msg)
                toast("发送信息 ${if (hex) "HEX" else "TEXT"} : $msg")
                if (hex)
                    viewModel.spUtil.writeHex(msg)
                else  viewModel.spUtil.writeStr(msg)
            } else {
                toast("尚未打开串口！")
            }
        }
        binding.infoTv.movementMethod = ScrollingMovementMethod()
    }

    fun checkHexString(string: String): Boolean {
        for (element in string) {
            var cInt: Int = element.toInt()
            if ((cInt in 48..57)
                || (cInt in 65..70)
                || (cInt in 97..102)
            ) {

            } else {
                return false
            }
        }
        return true
    }

    private fun initBaudAdapter() {
        val list = SerialPortUtil.BAUDS.map { it.toString() }
        baudAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item , list)
        baudAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.baudSp.adapter = baudAdapter!!
        binding.baudSp.setSelection(5)
        binding.baudSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Timber.i("HELTAG onItemSelected $position $id")
                viewModel.spUtil.iBaud = list[position].toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun initNameAdapter() {
        val list = viewModel.getList()
        nameAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item , list)
        nameAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.nameSp.adapter = nameAdapter!!
        binding.nameSp.setSelection(0)
        binding.nameSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.spUtil.sp = list[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    override fun initData() {
        viewModel.initData {
            toast(it)
        }
//        val info = StringBuilder()
//        for (i in 0 until 100) {
//            info.append("$i \n")
//        }
//        toast(info.toString())
    }

    @Inject
    override lateinit var viewModel: ComViewModel
    override fun linkViewModel() {
        binding.vm = viewModel
    }

    private fun toast(msg: String) {
        viewModel.viewModelScope.launch(Dispatchers.Main) {
            binding.infoTv.append(msg + "\n")
            var sc = binding.infoTv.layout.getLineTop(binding.infoTv.lineCount) - binding.infoTv.height
            if (sc > 0)
                binding.infoTv.scrollTo(0, sc)
            else binding.infoTv.scrollTo(0, 0)
        }
    }

}