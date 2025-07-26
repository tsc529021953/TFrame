package com.sc.tmp_translate.view

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.get
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
 *
 */
class TransMainFragment : BaseTransFragment<FragmentTransMainBinding, TransMainViewModel>() {

    @Inject
    override lateinit var viewModel: TransMainViewModel

    override var layoutId: Int = R.layout.fragment_trans_main

    private lateinit var adapter: ArrayAdapter<String>

    private var languageObsListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            activity?.runOnUiThread {
                refreshLanguage()
            }
        }
    }

    override fun linkViewModel() {
        binding.vm = viewModel
        if (TmpServiceDelegate.getInstance() != null)
            binding.tmp = TmpServiceDelegate.getInstance()
    }

    override fun subscribeUi() {
        firstView = true
        initSpinner()
        refreshLanguage()
    }

    override fun onFontSizeChanged(fontSize: Float) {
        setFontSize(binding.tranTv, R.dimen.main_text_size)
        setFontSize(binding.selectTv, R.dimen.main_text_size2)
        setFontSize(binding.leftLangTv, R.dimen.main_text_size)
//        setFontSize(binding.rightLangTv, R.dimen.main_text_size)
        adapter.notifyDataSetChanged()
    }

    override fun onLiveEB(cmd: String, data: String) {
        when (cmd) {
            MessageConstant.CMD_BIND_SUCCESS -> {
                binding.tmp = TmpServiceDelegate.getInstance()
                TmpServiceDelegate.getInstance().getTransLangObs()?.addOnPropertyChangedCallback(languageObsListener)
                refreshLanguage()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TmpServiceDelegate.getInstance().getTransLangObs()?.removeOnPropertyChangedCallback(languageObsListener)
    }

    override fun initData() {

    }

    private fun initSpinner() {
        adapter = object : ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, activity!!.getStringArray(R.array.lang_an_array)) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                setFontSize(view, R.dimen.main_text_size)
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                setFontSize(view, R.dimen.main_text_size)
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.rightLangSp.adapter = adapter
        binding.rightLangSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val lang = p0?.getItemAtPosition(p2).toString()
                System.out.println("异显：2 setTransLang $lang")
                TmpServiceDelegate.getInstance().setTransLang(lang)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    fun refreshLanguage() {
        val language = TmpServiceDelegate.getInstance().getTransLangObs()?.get() ?: return
        System.out.println("异显：2 $language")
        val index = activity!!.getStringArray(R.array.lang_an_array).indexOf(language)
        if (index >= 0)
            binding.rightLangSp.setSelection(index)
    }
}
