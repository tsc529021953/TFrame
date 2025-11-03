package com.sc.tmp_translate.view

import android.view.View
import androidx.databinding.Observable
import androidx.recyclerview.widget.LinearLayoutManager
import com.sc.tmp_translate.R
import com.sc.tmp_translate.adapter.TransTextAdapter
import com.sc.tmp_translate.base.BaseTransFragment
import com.sc.tmp_translate.da.TransRepository
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.da.TransRecordRepository
import com.sc.tmp_translate.databinding.FragmentTranslatingBinding
import com.sc.tmp_translate.inter.ITransRecord
import com.sc.tmp_translate.service.TmpServiceDelegate
import com.sc.tmp_translate.vm.TranslatingViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

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

    private var iTransRecord = object : ITransRecord {

        override fun onRecordEnd(isMaster: Boolean, path: String?) {

        }

        override fun onReceiveRes(isMaster: Boolean, path: String?, resList: List<String>) {

        }

        override fun onReceiveToView(isMaster: Boolean, pcm: ByteArray?, pcmList: List<ByteArray>?) {
            val cb = { bytes: ByteArray? ->
                if (bytes != null) {
                    val amp = viewModel!!.calculateAmplitude(bytes) * 5
//                    this@TranslatingODFragment.activity?.runOnUiThread {
                    if (isMaster)
                        binding.audioWaveView.addAmplitude(amp)
                    else {
                        if (TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() == false)
                            binding.audioWaveView2.addAmplitude(amp)
                        else{
                            // TODO 显示到另一边
                        }
                    }
//                    }
                }
            }
            if (pcm != null) {
                viewModel.mScope.launch {
                    cb.invoke(pcm)
                }
            }
        }

        override fun onTransStateChange(isMaster: Boolean, isTrans: Boolean) {
            this@TranslatingFragment.activity?.runOnUiThread {
                if (isMaster)
                    binding.translate1Iv.visibility = if (isTrans) View.VISIBLE else View.INVISIBLE
                else {
                    if (TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() == false)
                        binding.translate2Iv.visibility = if (isTrans) View.VISIBLE else View.INVISIBLE
                    else {
                        // TODO 显示到另一边
                    }
                }
            }
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

//    override fun onResume() {
//        super.onResume()
//        if (this::adapter.isInitialized) {
//            val data = if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true) TransRepository.getData().toMutableList()
//            else TransRecordRepository.getData().toMutableList()
//            adapter.setNewInstance(data)
//            System.out.println("刷新数据")
//        }
//    }

    override fun subscribeUi() {
        System.out.println("翻译界面初始化")
        val data = if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true) {
            TmpServiceDelegate.getInstance().changeTranslatingState(true)
            TransRepository.getData().toMutableList()
        }
        else TransRecordRepository.getData().toMutableList()
        adapter = TransTextAdapter(data, TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() ?: false, true) { v, id ->
            setFontSize(v, id)
        }
        binding.dataRv.adapter = adapter
        val layoutManager = LinearLayoutManager(activity!!)
        binding.dataRv.layoutManager = layoutManager
        TmpServiceDelegate.getInstance().getMoreDisplayObs()?.addOnPropertyChangedCallback(displayObsListener)
        TmpServiceDelegate.getInstance().addITransRecord(iTransRecord)
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
        System.out.println("翻译界面释放")
        if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true) {
            TransRepository.removeObserver(observer)
            TmpServiceDelegate.getInstance().changeTranslatingState(false)
        }

        TmpServiceDelegate.getInstance().getMoreDisplayObs()?.removeOnPropertyChangedCallback(displayObsListener)
        TmpServiceDelegate.getInstance().setTranslating(false)
        TmpServiceDelegate.getInstance().setTransState(false, 0)
        TmpServiceDelegate.getInstance().setTransRecord(false)
        TmpServiceDelegate.getInstance().removeITransRecord(iTransRecord)
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
        if (res) {
            binding.lineV.visibility = View.GONE
//            binding.audioWaveView2.visibility = View.GONE
            binding.translate2Iv.visibility = View.GONE
        } else {
            binding.lineV.visibility = View.VISIBLE
//            binding.audioWaveView2.visibility = View.VISIBLE
            binding.translate2Iv.visibility = View.VISIBLE
        }
    }
}
