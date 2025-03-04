package com.sc.tmp_cw.activity

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.dialog.TipDialog
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.widget.WrapLinearLayoutManager
import com.sc.tmp_cw.R
import com.sc.tmp_cw.adapter.PlaylistAdapter
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.ActivityPlaylistBinding
import com.sc.tmp_cw.vm.PlaylistViewModel
import com.sc.tmp_cw.weight.SimpleItemTouchHelperCallback
import timber.log.Timber
import java.util.*
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2025/1/10 15:00
 * @version 0.0.0-1
 * @description
 */
@Route(path = MessageConstant.ROUTH_PLAYLIST)
class PlatylistActivity : BaseBindingActivity<ActivityPlaylistBinding, PlaylistViewModel>(),PlaylistAdapter.FileImgCallback  {

    override var layoutId: Int = R.layout.activity_playlist

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_URGENT_NOTICE -> {
//                finish()
            }
        }
    }

    var adapter: PlaylistAdapter? = null

    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        adapter = PlaylistAdapter(viewModel.videoListObs.value ?: arrayListOf(), this)
        val ly = WrapLinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        val line = resources.getDimension(R.dimen.travel_btn_ly_space).toInt()
//        binding.rv.addItemDecoration(SpaceItemDecoration(arrayListOf(line, 0, line, line)))
        binding.rv.adapter = adapter
        binding.rv.layoutManager = ly

        initDrag()
    }

    private fun initDrag() {
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter) { p1, p2 ->
            //        Collections.swap(dataList, fromPosition, toPosition);
            if (p1 < p2) {
                for (i in p1 until p2) {
                    Collections.swap(adapter!!.data, i, i + 1)
                }
            } else {
                for (i in p1 downTo p2 + 1) {
                    Collections.swap(adapter!!.data, i, i - 1)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rv)
    }

    override fun initData() {
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
        viewModel.videoListObs.observe(this) {
            if (it == null) return@observe
            adapter?.setNewInstance(it)
        }
        viewModel.initData()
    }

    override fun linkViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
    }

    @Inject
    override lateinit var viewModel: PlaylistViewModel

    override fun onItemClick(item: FileBean) {
        val position = adapter!!.data!!.indexOf(item)
        if (position != -1) {
            val status = adapter!!.data!![position].status
            if (status == 0) {
                adapter!!.data!![position].status = 1
            } else adapter!!.data!![position].status = 0
            adapter!!.notifyItemChanged(position)
        }
    }

    override fun finish() {
        // 弹出框
        TipDialog.showInfoTip(this, getString(R.string.title_tip), getString(R.string.setting_is_save_or_cancel)
            , getString(R.string.text_no), getString(R.string.text_yes), {
                // 保存
                val list = ArrayList<FileBean>()
                val last = viewModel.spManager.getString(MessageConstant.SP_PLAYLIST_CHECK, "")
                val sb = StringBuilder()
                adapter!!.data!!.forEach {
                    if (it.status == 1) {
                        list.add(it)
                        Timber.i("path ${it.path}")
                        sb.append(it.path)
                    }
                }
                val str = sb.toString()
                Timber.i("path $str $last")
                if (last != str) {
                    viewModel.spManager.setString(MessageConstant.SP_PLAYLIST_CHECK, str)
                    viewModel.spManager.setString(MessageConstant.SP_PLAYLIST, viewModel.gson.toJson(list))
                } else {
                    Timber.i("数据未变化，无需保存！")
                }

                super.finish()
                return@showInfoTip true
            }, {
                super.finish()
                return@showInfoTip true
            })
    }
}
