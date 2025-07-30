package com.sc.tmp_translate.view

import androidx.databinding.Observable
import androidx.recyclerview.widget.LinearLayoutManager
import com.sc.tmp_translate.R
import com.sc.tmp_translate.adapter.TransTextAdapter
import com.sc.tmp_translate.base.BaseTransFragment
import com.sc.tmp_translate.da.TransRepository
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.databinding.FragmentTranslatingBinding
import com.sc.tmp_translate.service.TmpServiceDelegate
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

    private val observer: (List<TransTextBean>) -> Unit = { items ->
        adapter.submitList(items)
        // 滚动到底部（需要 post 保证更新后执行）
        binding.dataRv.post {
            binding.dataRv.scrollToPosition(items.size - 1)
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
        adapter = TransTextAdapter(TransRepository.getData().toMutableList(), TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() ?: false, true) { v, id ->
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
        setFontSize(binding.leftLangTv, R.dimen.main_text_size1)
        setFontSize(binding.rightLangTv, R.dimen.main_text_size1)
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
        TransRepository.removeObserver(observer)
        TmpServiceDelegate.getInstance().getMoreDisplayObs()?.removeOnPropertyChangedCallback(displayObsListener)
        TmpServiceDelegate.getInstance().setTranslating(false)
    }

    override fun initData() {
        TransRepository.addObserver(observer)
    }

    fun refreshDisplay() {
        val res = TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() ?: false
        if (res != adapter.isMore) {
            adapter.isMore = res
            adapter.notifyDataSetChanged()
        }
    }
}
