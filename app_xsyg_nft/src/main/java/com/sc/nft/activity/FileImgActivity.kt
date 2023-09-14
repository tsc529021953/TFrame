package com.sc.nft.activity

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Observable
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.nbhope.lib_frame.app.AppManager
import com.nbhope.lib_frame.arouterpath.RouterPath
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.constants.GlobalScopeUtils
import com.nbhope.lib_frame.widget.WrapGridLayoutManager
import com.sc.nft.R
import com.sc.nft.adapter.FileImgAdapter
import com.sc.nft.bean.FileBean
import com.sc.nft.bean.FileImgBean
import com.sc.nft.databinding.ActivityFileImg2Binding
import com.sc.nft.databinding.ActivityFileImg3Binding
import com.sc.nft.databinding.ActivityFileImgBinding
import com.sc.nft.vm.MainViewModel
import com.sc.nft.weight.GridSpaceItemDecoration
import kotlinx.coroutines.delay
import pl.droidsonroids.gif.GifDrawable
import timber.log.Timber

/**
 * author: sc
 * date: 2023/9/10
 */
@Route(path = RouterPath.activity_nft_file)
class FileImgActivity : NFTBaseActivity<ActivityFileImgBinding, BaseViewModel>() {

    override var layoutId: Int = R.layout.activity_file_img

    private var adapter: FileImgAdapter? = null

    private var fileBean: FileBean = FileBean()

    @Autowired
    @JvmField
    var filename: String = ""

    @Autowired
    @JvmField
    var file: String = ""

    @Autowired
    @JvmField
    var name: String = ""

    @Autowired
    @JvmField
    var index: Int = 0

    @Autowired
    @JvmField
    var type: Int = 0

    lateinit var callback : ((fileBean: FileBean) -> Unit)

    var fileImgList = ArrayList<FileImgBean>()

    override fun subscribeUi() {
        super.subscribeUi()
        Timber.i("NTAG param " +
                " filename: $filename" +
                " index: $index " +
                " file: $file ")
        callback = {
            this.fileBean.postValue(it)
            this.runOnUiThread {
                initView()
            }
        }

        // 获取根路径下所有文件夹和文件
        fileBean.fileName = filename
        fileBean.file = file
        fileBean.name.set(name)
        fileBean.type.set(type)
        if (index != -1)
            fileBean.index = index
        fileImgList = MainViewModel.getInstance().fileImgList

        binding.vm = fileBean
        binding.homeIv.setOnClickListener {
            Timber.i("NTAG homeIv home")
            AppManager.appManager?.killAll("com.sc.nft.activity.MainActivity")
        }
        binding.backIv.setOnClickListener {
            finish()
        }
        binding.nextIv.setOnClickListener {
            prev(it)
        }
        binding.prevIv.setOnClickListener {
            next(it)
        }

        val layoutManager: LinearLayoutManager = WrapGridLayoutManager(this, 2, LinearLayoutManager.VERTICAL
            , true)
        binding.imgRv.layoutManager = layoutManager
        binding.imgRv.addItemDecoration(GridSpaceItemDecoration(2 , 0, 0))
        adapter = FileImgAdapter(fileBean.files, object : FileImgAdapter.FileImgCallback{
            override fun onItemClick(item: FileImgBean) {
                navigation(item)
            }
        })
        binding.imgRv.adapter = adapter
    }

    private fun initView() {
        if (fileBean.type.get() == MainViewModel.TYPE_UN_INIT) return
        Timber.i("NTAG initView ${fileBean.type.get()}")
        if (fileBean.type.get() == MainViewModel.TYPE_IMAGE) {
            imgLoad()
        } else {
           runOnUiThread {
               adapter?.setList(fileBean.files)
           }
//            val size = fileBean.files.size
//            var count = if (size > 2) {
//                if (size % 3 == 0) 3 else 4
//            } else 2
//            count = 2
//            val layoutManager: LinearLayoutManager = WrapGridLayoutManager(this, 2, LinearLayoutManager.VERTICAL
//                , true)
//            binding.imgRv.layoutManager = layoutManager
        }
    }

    fun navigation(item: FileImgBean) {
        // 记录下跳转的
        MainViewModel.getInstance().fileImgList = fileBean.files
        val index = fileBean.files.indexOf(item)
        Timber.i("NTAG index $index")
        ARouter.getInstance().build(RouterPath.activity_nft_file)
            .withString(MainViewModel.FILENAME, item.filename)
            .withString(MainViewModel.FILE, item.file)
            .withString(MainViewModel.NAME, item.name)
            .withInt(MainViewModel.INDEX, if (index == -1) 0 else index)
            .navigation(this)
    }

    override fun onResume() {
        super.onResume()
        MainViewModel.getInstance().load(fileBean, callback)
    }

    private fun imgLoad() {
        Timber.i("NTAG image ${fileBean.image}")
        val img = fileBean.image ?: return
        if (img.isNullOrEmpty()) return
        // 图片加载
        if (img.endsWith(".gif")) {
            try {
                val gifFromAssets = GifDrawable(img)
                binding.imgIv.setImageDrawable(gifFromAssets)
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("NTAG err $e")
            }
        } else {
            Glide.with(binding.imgIv)
                .load(img)
                .error(R.mipmap.logo_move)
//                            .placeholder(R.mipmap.uhome_ic_device_default)
                .into(binding.imgIv)
        }
    }

    override var viewModel: BaseViewModel = BaseViewModel()

    fun next(view: View) {
        val index = fileBean.index + 1
        val max = fileImgList.size
        if (index >= max) {
            ToastUtils.showShort("已到最后一个！")
            return
        }
        move(fileImgList[index], index)
    }

    private fun prev(view: View) {
        val index = fileBean.index - 1
        if (index < 0) {
            ToastUtils.showShort("已到最开始位置！")
            return
        }
        move(fileImgList[index], index)
    }

    private fun move(item: FileImgBean, index: Int) {
        Timber.i("NTAG move bef $index ${fileBean.index} $item")
        fileBean.index = index
//        fileBean.type.set(MainViewModel.TYPE_UN_INIT)
        fileBean.name.set(item.name)
        fileBean.file = item.file!!
        fileBean.fileName = item.filename!!
        fileBean.loaded = false
        binding.handLy.restoreOriginalState()
        Timber.i("NTAG move $fileBean")
        MainViewModel.getInstance().load(fileBean, callback)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
