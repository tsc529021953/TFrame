package com.sc.tmp_translate.view

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.Observable
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.sc.tmp_translate.R
import com.sc.tmp_translate.base.BaseTransFragment
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.databinding.FragmentTransMainBinding
import com.sc.tmp_translate.service.TmpServiceDelegate
import com.sc.tmp_translate.service.TmpServiceImpl
import com.sc.tmp_translate.vm.TransMainViewModel
import javax.inject.Inject

/**
 * TODO 初始状态获取  下拉菜单大小跟随
 */
class TransMainFragment : BaseTransFragment<FragmentTransMainBinding, TransMainViewModel>() {

    @Inject
    override lateinit var viewModel: TransMainViewModel

    override var layoutId: Int = R.layout.fragment_trans_main

    private lateinit var adapter: ArrayAdapter<String>

    override fun linkViewModel() {
        binding.vm = viewModel
        if (TmpServiceDelegate.getInstance() != null)
            binding.tmp = TmpServiceDelegate.getInstance()
    }

    override fun subscribeUi() {
        firstView = true
//        adapter = ArrayAdapter.createFromResource(
//            requireContext(), R.array.lang_an_array, android.R.layout.simple_spinner_item
//        )
        adapter = object : ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, activity!!.getStringArray(R.array.lang_an_array)) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
//                view.textSize =  getFontSize()
                setFontSize(view, R.dimen.main_text_size1)
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                setFontSize(view, R.dimen.main_text_size1)
                return view
            }
        }
//        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item,
//            activity!!.getStringArray(R.array.lang_an_array)) {
//        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.rightLangSp.adapter = adapter
        binding.rightLangSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val lang = p0?.getItemAtPosition(p2).toString()
                System.out.println("lang $lang")
                TmpServiceDelegate.getInstance().setTransLang(lang)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    override fun onFontSizeChanged(fontSize: Float) {
        setFontSize(binding.tranTv, R.dimen.main_text_size)
        setFontSize(binding.selectTv, R.dimen.main_text_size2)
        setFontSize(binding.leftLangTv, R.dimen.main_text_size)
        setFontSize(binding.rightLangTv, R.dimen.main_text_size)
//        System.out.println("child: ${binding.rightLangSp.childCount} ${adapter.getView(0, binding.rightLangSp)}")
        adapter.notifyDataSetChanged()
    }

    override fun onLiveEB(cmd: String, data: String) {
        when (cmd) {
            MessageConstant.CMD_BIND_SUCCESS -> {
                binding.tmp = TmpServiceDelegate.getInstance()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun initData() {

    }
}