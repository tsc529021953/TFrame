package com.sc.tmp_cw.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.HopeUtils
import com.nbhope.lib_frame.view.SpaceItemDecoration
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.sc.tmp_cw.R
import com.sc.tmp_cw.adapter.FileImgAdapter
import com.sc.tmp_cw.constant.MessageConstant
import com.sc.tmp_cw.databinding.FragmentTravelBinding
import com.sc.tmp_cw.vm.IntroduceViewModel
import com.sc.tmp_cw.vm.TravelViewModel
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/6 19:38
 * @version 0.0.0-1
 * @description
 */
class TravelFragment: BaseBindingFragment<FragmentTravelBinding, TravelViewModel>(), FileImgAdapter.FileImgCallback {

    override var layoutId: Int = R.layout.fragment_travel

    @Inject
    override lateinit var viewModel: TravelViewModel

    var adapter: FileImgAdapter? = null

    override fun linkViewModel() {

    }


    override fun subscribeUi() {
        binding.backBtn.setOnClickListener {
            InteractiveFragment.iFragment?.back()
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

    override fun onItemClick(item: FileBean) {
        IntroduceViewModel.fileBean = item
        IntroduceViewModel.bigList = viewModel.bigList
        IntroduceViewModel.videoList = viewModel.videoList
        IntroduceViewModel.textList = viewModel.textList
        ARouter.getInstance().build(MessageConstant.ROUTH_INTRODUCE).navigation(requireContext())
    }


}
