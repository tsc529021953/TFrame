package com.sc.tmp_translate.view

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.Observable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.sc.tmp_translate.R
import com.sc.tmp_translate.adapter.TransTextAdapter
import com.sc.tmp_translate.base.BaseTransFragment
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.databinding.FragmentTransMainBinding
import com.sc.tmp_translate.databinding.FragmentTranslatingBinding
import com.sc.tmp_translate.service.TmpServiceDelegate
import com.sc.tmp_translate.vm.TransMainViewModel
import com.sc.tmp_translate.vm.TranslatingViewModel
import javax.inject.Inject

/**
 *
 */
class TranslatingFragment : BaseTransFragment<FragmentTranslatingBinding, TranslatingViewModel>() {

    @Inject
    override lateinit var viewModel: TranslatingViewModel

    override var layoutId: Int = R.layout.fragment_translating

    lateinit var adapter: TransTextAdapter

    private var displayObsListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            activity?.runOnUiThread {
                refreshDisplay()
            }
        }
    }

    override fun linkViewModel() {
        binding.vm = viewModel
        if (TmpServiceDelegate.getInstance() != null)
            binding.tmp = TmpServiceDelegate.getInstance()
    }

    override fun subscribeUi() {
        val arr: ArrayList<TransTextBean> = arrayListOf()
        val b1 = TransTextBean()
        b1.text = "77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777"
        b1.transText = "？？？？？？？？？？？？？？？555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555"
        val b2 = TransTextBean()
        b2.text = "？？？？？？？？？？？？？？？555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555"
        b2.transText = "77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777"
        b1.isMaster = false

        arr.add(b1)
        arr.add(b2)

        adapter = TransTextAdapter(arr, TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() ?: false, true) { v, id ->
            setFontSize(v, id)
        }
        binding.dataRv.adapter = adapter
        val layoutManager = LinearLayoutManager(activity!!)
        binding.dataRv.layoutManager = layoutManager
//        binding.dataRv.addItemDecoration(NormaltemDecoration(KGItemAdapter.getD2P(6, this)))
        TmpServiceDelegate.getInstance().getMoreDisplayObs()?.addOnPropertyChangedCallback(displayObsListener)
    }

    override fun onFontSizeChanged(fontSize: Float) {
//        setFontSize(binding.tranTv, R.dimen.main_text_size)
        setFontSize(binding.leftLangTv, R.dimen.main_text_size)
        setFontSize(binding.rightLangTv, R.dimen.main_text_size)
        adapter.notifyDataSetChanged()
    }

    override fun onLiveEB(cmd: String, data: String) {
        when (cmd) {
            MessageConstant.CMD_BIND_SUCCESS -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TmpServiceDelegate.getInstance().getMoreDisplayObs()?.removeOnPropertyChangedCallback(displayObsListener)
        TmpServiceDelegate.getInstance().setTranslating(false)
    }

    override fun initData() {

    }

    fun refreshDisplay() {
        val res = TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() ?: false
        if (res != adapter.isMore) {
            adapter.isMore = res
            adapter.notifyDataSetChanged()
        }
    }
}
