package com.sc.tmp_translate.view

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.nbhope.lib_frame.utils.ViewUtil
import com.sc.tmp_translate.R
import com.sc.tmp_translate.adapter.TransTextAdapter
import com.sc.tmp_translate.da.TransRepository
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.da.TransRecordRepository
import com.sc.tmp_translate.databinding.DisplayTransMoreBinding
import com.sc.tmp_translate.service.TmpServiceDelegate
import java.util.*

//, display
class TransMoreDisplay(context: Context , display: Display , var fontSizeCB: ((view: View, id: Int) -> Unit)) : Presentation(context, display) {

    private var binding: DisplayTransMoreBinding? = null

    private lateinit var adapter: ArrayAdapter<String>

    lateinit var adapter2: TransTextAdapter

    private val observer: (List<TransTextBean>) -> Unit = { items ->
        adapter2.submitList(items)
        // 滚动到底部（需要 post 保证更新后执行）
        binding?.dataRv?.post {
            binding?.dataRv?.scrollToPosition(items.size - 1)
        }
    }

    var isUserInitiated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.display_trans_more,
            null,
            false
        )
        binding?.root?.let { setContentView(it) }
//        window?.setLayout(
//                1800, // 宽度
//                960  // 高度
//        )
        refreshTmp()
        initSpinner()
        initTranslating()
//        if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true)
            TransRepository.addObserver(observer)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
//        if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true)
            TransRepository.removeObserver(observer)
    }

    fun onFontSizeChanged() {
        if (binding == null) return
        fontSizeCB(binding!!.tranTv, R.dimen.main_text_size1)
        fontSizeCB(binding!!.selectTv, R.dimen.main_text_size2)
        fontSizeCB(binding!!.leftLangTv, R.dimen.main_text_size)
        adapter.notifyDataSetChanged()

        fontSizeCB(binding!!.rightLangTv, R.dimen.main_text_size1)
        fontSizeCB(binding!!.leftLang2Tv, R.dimen.main_text_size1)
        adapter2.notifyDataSetChanged()
    }

    fun showFun() {
        if (isShowing) return
        show()
        refreshLanguage()
        onFontSizeChanged()
    }

    fun hideFun() {
        if (isShowing) hide()
    }

    fun refreshTmp() {
        if (TmpServiceDelegate.getInstance().getTranslatingObs() != null)
            binding?.tmp = TmpServiceDelegate.getInstance()
    }

    private fun initSpinner() {
        adapter = object : ArrayAdapter<String>(context , android.R.layout.simple_spinner_item, context.getStringArray(R.array.lang_more_array)) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                fontSizeCB(view, R.dimen.main_text_size)
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(context.resources.getColor(R.color.text_color))
                fontSizeCB(view, R.dimen.main_text_size)
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.rightLangSp.adapter = adapter
        binding!!.rightLangSp.setOnTouchListener { _, _ ->
            isUserInitiated = true
            false // 允许 Spinner 正常处理触摸事件
        }
        binding!!.rightLangSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (isUserInitiated) {
                    val lang = p0?.getItemAtPosition(p2).toString()
                    val languages = context.getStringArray(R.array.lang_an_array)
                    val moreLanguages = context.getStringArray(R.array.lang_more_array)
                    val index = moreLanguages.indexOf(lang)
                    log("setTransLang ${languages[index]}")
                    TmpServiceDelegate.getInstance().setTransLang(languages[index])
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun initTranslating() {
        val data = if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true) TransRepository.getData().toMutableList()
        else TransRecordRepository.getData().toMutableList()
        adapter2 = TransTextAdapter(data, TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() ?: false, false) { v, id ->
            fontSizeCB(v, id)
        }
        binding?.dataRv?.adapter = adapter2
        val layoutManager = LinearLayoutManager(context)
        binding?.dataRv?.layoutManager = layoutManager
    }

    fun refreshData() {
        if (this::adapter2.isInitialized) {
            val data = if (TmpServiceDelegate.getInstance().getTransRecordObs()?.get() != true) TransRepository.getData().toMutableList()
            else TransRecordRepository.getData().toMutableList()
            adapter2.setNewInstance(data)
        }
    }

    fun refreshLanguage() {
        if (binding == null) return
        val language = TmpServiceDelegate.getInstance().getTransLangObs()?.get() ?: return
        log("refreshLanguage $language")
        val index = context!!.getStringArray(R.array.lang_an_array).indexOf(language)
        if (index >= 0)
            binding?.rightLangSp?.setSelection(index)

        val exLanguages = context.getStringArray(R.array.lang_ex_array)
        val ex = exLanguages[index]
        val t1 = ViewUtil.getLocalizedString(context, R.string.language_zn, Locale(ex))
        binding!!.leftLangTv.text = t1
        binding!!.leftLang2Tv.text = t1
        binding!!.tranTv.text = ViewUtil.getLocalizedString(context, R.string.touch_start_trans, Locale(ex))
        binding!!.selectTv.text = ViewUtil.getLocalizedString(context, R.string.touch_select_trans, Locale(ex))

        // 右上角
        val moreLanguages = context.getStringArray(R.array.lang_more_array)
        binding?.rightLangTv?.text = moreLanguages[index]
    }

    fun log(msg: Any?) {
        System.out.println("异显：${msg ?: "null"}")
    }
}
