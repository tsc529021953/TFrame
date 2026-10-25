package com.sc.tmp_cw.view

import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.HandleClick
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.sc.tmp_cw.R
import com.sc.tmp_cw.adapter.FileImgAdapter
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentTravelBinding
import com.sc.tmp_cw.inter.TimerIntercept
import com.sc.tmp_cw.utils.FlavorConfigUtil
import com.sc.tmp_cw.vm.IntroduceViewModel
import com.sc.tmp_cw.vm.TravelViewModel
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/6 19:38
 * @version 0.0.0-1
 * @description
 */
class TravelFragment: BaseBindingFragment<FragmentTravelBinding, TravelViewModel>(), FileImgAdapter.FileImgCallback, TimerIntercept {

    override var layoutId: Int = R.layout.fragment_travel

    @Inject
    override lateinit var viewModel: TravelViewModel

    var adapter: FileImgAdapter? = null

    private var handleClick: HandleClick? = null

    /** 方案2(flavorB)页内详情浮层控制器 */
    private var detailController: InPageDetailController? = null

    override fun linkViewModel() {

    }


    override fun subscribeUi() {
        if (handleClick == null)
            handleClick = HandleClick(500, 2)
        detailController = InPageDetailController(this, viewModel.viewModelScope,
                DetailOverlayViews(binding.detailLy, binding.detailIv, binding.detailTitleTv,
                        binding.detailUnderlineV, binding.detailTextTv))
        binding.backBtn.setOnClickListener {
            if (detailController?.isShown() == true) detailController?.close()
            else InteractiveFragment.iFragment?.back()
        }
        // 浮层内任何触摸都重置父页面的无操作计时,避免阅读时被自动导航回首页
        binding.detailLy.setOnTouchListener { _, _ ->
            InteractiveFragment.iFragment?.clicked()
            return@setOnTouchListener false
        }
        // 点击浮层空白处关闭
        binding.detailLy.setOnClickListener {
            handleClick?.handle {
                detailController?.close()
            }
        }
        adapter = FileImgAdapter(viewModel.smallListObs.value ?: arrayListOf(), this)
        val ly = WrapGridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, true)
        val line = resources.getDimension(R.dimen.travel_btn_ly_space).toInt()
        binding.rv.addItemDecoration(SpaceItemDecoration(arrayListOf(line, 0, line, line)))
        binding.rv.adapter = adapter
        binding.rv.layoutManager = ly
    }

    override fun initData() {
        viewModel.smallListObs.observe(this) {
            if (it == null) return@observe
            adapter?.setNewInstance(it)
        }
        viewModel.initData()

    }

    override fun onItemClick(item: FileBean, sharedIv: View?) {
        if (FlavorConfigUtil.isFlavorB() && sharedIv != null) {
            // 方案2: 视频条目仍走全屏播放页;图片条目在本页浮层展示
            val video = viewModel.videoList?.find { it.name.startsWith(item.name) }
            if (video != null) {
                jumpToIntroduce(item)
            } else {
                InteractiveFragment.iFragment?.clicked()
                detailController?.show(item, viewModel.bigList, viewModel.textList, sharedIv)
            }
        } else {
            jumpToIntroduce(item)
        }
    }

    private fun jumpToIntroduce(item: FileBean) {
        IntroduceViewModel.fileBean = item
        IntroduceViewModel.bigList = viewModel.bigList
        IntroduceViewModel.videoList = viewModel.videoList
        IntroduceViewModel.textList = viewModel.textList
        ARouter.getInstance().build(MessageConstant.ROUTH_INTRODUCE).navigation(requireContext())
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        // 页面被切走时直接收起浮层,不播动画
        if (hidden && isAdded) detailController?.reset()
    }

    override fun onPause() {
        super.onPause()
        if (isAdded) detailController?.reset()
    }

    /** 定时器到达:浮层打开时优先关闭浮层并由父页面重新计时,再次到达才退出本页 */
    override fun onTimerReach(): Boolean = detailController?.onTimerReach() == true

    override fun onDestroyView() {
        detailController?.reset()
        super.onDestroyView()
    }

}
