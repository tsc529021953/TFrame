package com.nbhope.lib_frame.activity

import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.widget.ScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.nbhope.lib_frame.R
import com.nbhope.lib_frame.adapter.FileBeanAdapter
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.common.BasePath
import com.nbhope.lib_frame.databinding.ActivityLogBinding
import com.nbhope.lib_frame.utils.ValueHolder
import com.nbhope.lib_frame.viewmodel.LogViewModel
import com.nbhope.lib_frame.widget.WrapLinearLayoutManager
import com.nbhope.lib_frame.widget.txt.SwanTextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.CharBuffer


/**
 * @author  tsc
 * @date  2025/1/15 9:27
 * @version 0.0.0-1
 * @description
 */
@Route(path = BasePath.LOG_PATH)
class LogActivity : BaseBindingActivity<ActivityLogBinding, LogViewModel>(), FileBeanAdapter.FileBeanCallback, SwanTextView.OnTextChangedListener {

    override var layoutId: Int = R.layout.activity_log

    var adapter: FileBeanAdapter? = null

    private val LOG_TAG = "TxtReader"
    private val SHOW_TXT = 1

    private lateinit var mTextShow: SwanTextView
    private lateinit var mScrollView: ScrollView
    private lateinit var mStringShow: String

    private var mContinueRead = true
    private var mHaveNewText = false

    private var mCurBottom = -1
    private var mNum = -1

    override fun subscribeUi() {
        binding.drawerLy.openDrawer(Gravity.LEFT)
        binding.backIv.setOnClickListener {
            finish()
        }
        binding.listIv.setOnClickListener {
            binding.drawerLy.openDrawer(Gravity.LEFT)
        }
        adapter = FileBeanAdapter(arrayListOf(), this)
        val ly = WrapLinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        val line = resources.getDimension(R.dimen.travel_btn_ly_space).toInt()
//        binding.rv.addItemDecoration(SpaceItemDecoration(arrayListOf(line, 0, line, line)))
        binding.rv.adapter = adapter
        binding.rv.layoutManager = ly

        mTextShow = binding.logTv
        mTextShow?.setOnTextChangedListener(this)
        mScrollView = binding.sv
    }

    override fun initData() {

    }

    override fun linkViewModel() {
        viewModel = ViewModelProvider(this)[LogViewModel::class.java]
        viewModel.initData()
        viewModel.textList.observe(this) {
            if (it == null) return@observe
            System.out.println("textList ${it.size}")
            adapter?.setNewInstance(it)
        }
    }


    override lateinit var viewModel: LogViewModel

    var task: TextShowTask? = null

    override fun onItemClick(item: FileBean) {
        ValueHolder.setValue {
            val cb = {
                task = TextShowTask {
                    System.out.println("TextShowTask 结束 ${item.path}")
                }
                task?.execute(item.path)
            }
            if (task == null) {
                cb.invoke()
            } else {
                task?.cancel = true
                viewModel.mScope.launch {
                    delay(1000)
                    mContinueRead = true
                    mHaveNewText = false
                    mCurBottom = -1
                    mNum = -1
                    cb.invoke()
                }
            }
        }
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SHOW_TXT -> mTextShow.setText(msg.obj as CharBuffer)
                else -> super.handleMessage(msg)
            }
        }
    }


    @Throws(IOException::class, InterruptedException::class)
    private fun showText(path: String) {

    }


    inner class TextShowTask(var endCB: () -> Unit) : AsyncTask<Any?, Any?, Any?>() {

        var cancel = false

        override fun onPostExecute(param: Any?) {
//            Log.d(LOG_TAG, "Send broadcast")
        }

        override fun doInBackground(vararg params: Any?): Any? {
            val path: String = params[0] as String
            cancel = false
            try {
//                showText(uri)

                val `is` = InputStreamReader(FileInputStream(
                        path), "UTF-8")
                val sb = StringBuilder()
                val buf = CharArray(1024 * 2)
                while (!cancel) {
                    if (mCurBottom === mScrollView.getScrollY()) {
//                        Log.d(LOG_TAG, "curBtm:" + mCurBottom.toString() + " scroll:"
//                                + mScrollView.getScrollY())
                        mCurBottom = -1
                        mNum++
                        if (mNum % 2 === 0) {
                            mContinueRead = true
//                            Log.d(LOG_TAG, "YES")
                        }
                    }
                    if (mContinueRead && `is`.read(buf) > 0) {
                        mContinueRead = false
                        if (sb.length > 4096) {
                            sb.delete(0, 2048)
                            val msg: Message = mHandler.obtainMessage(SHOW_TXT)
                            msg.obj = CharBuffer.wrap(sb.toString())
                            mHandler.sendMessage(msg)
                            mStringShow = sb.append(buf).toString()
                            mHaveNewText = true
                        } else {
                            while (sb.length < 4096) {
                                sb.append(buf)
                                `is`.read(buf)
                            }
                            sb.append(buf)
                            val msg: Message = mHandler.obtainMessage(SHOW_TXT)
                            msg.obj = CharBuffer.wrap(sb.toString())
                            mHandler.sendMessage(msg)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.d(LOG_TAG, "Exception", e)
            }
            endCB.invoke()
            return null
        }

        override fun onCancelled() {
            super.onCancelled()

        }
    }


    override fun onPreOnDraw(bottom: Int) {
        mCurBottom = bottom - mScrollView.getHeight()

        if (mHaveNewText && !mStringShow.isNullOrEmpty()) {
            mHaveNewText = false
            val msg: Message = mHandler.obtainMessage(SHOW_TXT)
            msg.obj = CharBuffer.wrap(mStringShow)
            mHandler.sendMessage(msg)
        }

    }
}
