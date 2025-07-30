package com.sc.tmp_translate.view

import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.ValueHolder
import com.sc.tmp_translate.R
import com.sc.tmp_translate.adapter.TransRecordAdapter
import com.sc.tmp_translate.adapter.TransTextAdapter
import com.sc.tmp_translate.base.BaseTransFragment
import com.sc.tmp_translate.bean.TransRecordBean
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.constant.MessageConstant.CMD_BACK
import com.sc.tmp_translate.da.RecordRepository
import com.sc.tmp_translate.da.TransRepository
import com.sc.tmp_translate.databinding.FragmentTransRecordBinding
import com.sc.tmp_translate.service.TmpServiceDelegate
import com.sc.tmp_translate.vm.TransRecordViewModel
import javax.inject.Inject

class TransRecordFragment : BaseTransFragment<FragmentTransRecordBinding, TransRecordViewModel>() {

    @Inject
    override lateinit var viewModel: TransRecordViewModel

    override var layoutId: Int = R.layout.fragment_trans_record

    private lateinit var adapter: TransRecordAdapter

    private val observer: (List<TransRecordBean>) -> Unit = { items ->
        val refresh = items.size < adapter.itemCount
        adapter.submitList(items)
        if (refresh) {
            adapter.notifyDataSetChanged()
        }
        // 滚动到底部（需要 post 保证更新后执行）
        binding.dataRv.post {
            binding.dataRv.scrollToPosition(items.size - 1)
        }
    }

    override fun onFontSizeChanged(fontSize: Float) {
        setFontSize(binding.titleTv, R.dimen.main_title_size)
        adapter.notifyDataSetChanged()
    }

    override fun onLiveEB(cmd: String, data: String) {
        
    }

    override fun linkViewModel() {
        
    }

    override fun subscribeUi() {
        adapter = TransRecordAdapter(RecordRepository.getData().toMutableList(), viewModel.spManager) { v, id ->
            setFontSize(v, id)
        }
        binding.dataRv.adapter = adapter
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.dataRv.layoutManager = layoutManager
        binding.backBtn.setOnClickListener {
            ValueHolder.setValue {
                LiveEBUtil.post(RemoteMessageEvent(CMD_BACK, ""))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RecordRepository.removeObserver(observer)
    }

    override fun initData() {
        RecordRepository.addObserver(observer)
    }
}