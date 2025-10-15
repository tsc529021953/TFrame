package com.sc.tmp_translate.view

import androidx.databinding.Observable
import androidx.recyclerview.widget.LinearLayoutManager
import com.sc.tmp_translate.R
import com.sc.tmp_translate.adapter.TransText2Adapter
import com.sc.tmp_translate.adapter.TransTextAdapter
import com.sc.tmp_translate.base.BaseTransFragment
import com.sc.tmp_translate.da.TransRepository
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.da.TransRecordRepository
import com.sc.tmp_translate.databinding.FragmentTranslatingBinding
import com.sc.tmp_translate.databinding.FragmentTranslatingOdBinding
import com.sc.tmp_translate.service.TmpServiceDelegate
import com.sc.tmp_translate.vm.TranslatingViewModel
import javax.inject.Inject

/**
 *
 */
class TranslatingODFragment : BaseTransFragment<FragmentTranslatingOdBinding, TranslatingViewModel>() {

    @Inject
    override lateinit var viewModel: TranslatingViewModel

    override var layoutId: Int = R.layout.fragment_translating_od

    lateinit var adapter: TransText2Adapter
    lateinit var adapter2: TransText2Adapter

    private val observer: (List<TransTextBean>) -> Unit = { items ->
        adapter.submitList(items)
        adapter2.submitList(items)
        // 滚动到底部（需要 post 保证更新后执行）
        binding.dataRv.post {
            binding.dataRv.scrollToPosition(items.size - 1)
        }
        binding.dataRv2.post {
            binding.dataRv2.scrollToPosition(items.size - 1)
        }
    }

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
        System.out.println("翻译界面初始化")
        val data = if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true) {
            TmpServiceDelegate.getInstance().changeTranslatingState(true)
            TransRepository.getData().toMutableList()
        }
        else TransRecordRepository.getData().toMutableList()
        adapter = TransText2Adapter(data, true, true) { v, id ->
            setFontSize(v, id)
        }
        adapter2 = TransText2Adapter(data, true, false) { v, id ->
            setFontSize(v, id)
        }
        binding.dataRv.adapter = adapter
        binding.dataRv2.adapter = adapter2
        val layoutManager = LinearLayoutManager(activity!!)
        val layoutManager2 = LinearLayoutManager(activity!!)
        binding.dataRv.layoutManager = layoutManager
        binding.dataRv2.layoutManager = layoutManager2
        TmpServiceDelegate.getInstance().getMoreDisplayObs()?.addOnPropertyChangedCallback(displayObsListener)
    }

    override fun onFontSizeChanged(fontSize: Float) {
//        setFontSize(binding.tranTv, R.dimen.main_text_size)
        setFontSize(binding.leftLangTv, R.dimen.main_text_size1)
        setFontSize(binding.rightLangTv, R.dimen.main_text_size1)
        adapter.notifyDataSetChanged()
        adapter2.notifyDataSetChanged()
    }

    override fun onLiveEB(cmd: String, data: String) {
        when (cmd) {
            MessageConstant.CMD_BIND_SUCCESS -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        System.out.println("翻译界面释放")
        if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true) {
            TransRepository.removeObserver(observer)
            TmpServiceDelegate.getInstance().changeTranslatingState(false)
        }

        TmpServiceDelegate.getInstance().getMoreDisplayObs()?.removeOnPropertyChangedCallback(displayObsListener)
        TmpServiceDelegate.getInstance().setTranslating(false)
        TmpServiceDelegate.getInstance().setTransState(false, 0)
        TmpServiceDelegate.getInstance().setTransRecord(false)
    }

    override fun initData() {
        if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true)
            TransRepository.addObserver(observer)
    }

    fun refreshDisplay() {
        val res = TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() ?: false
        if (res != adapter.isMore) {
            adapter.isMore = res
            adapter.notifyDataSetChanged()
        }
        if (res != adapter2.isMore) {
            adapter2.isMore = res
            adapter2.notifyDataSetChanged()
        }
    }
}
