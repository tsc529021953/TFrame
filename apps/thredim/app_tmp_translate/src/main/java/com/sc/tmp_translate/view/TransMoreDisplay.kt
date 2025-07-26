package com.sc.tmp_translate.view

import android.app.Dialog
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
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.databinding.DisplayOtherBinding
import com.sc.tmp_translate.databinding.DisplayTransMoreBinding
import com.sc.tmp_translate.service.TmpServiceDelegate
import java.util.*
import kotlin.collections.ArrayList

// , display: Display , display
class TransMoreDisplay(context: Context, var fontSizeCB: ((view: View, id: Int) -> Unit)) : Dialog(context) {

    private var binding: DisplayTransMoreBinding? = null

    private lateinit var adapter: ArrayAdapter<String>

    lateinit var adapter2: TransTextAdapter

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
        window?.setLayout(
                1100, // 宽度
                600  // 高度
        )
        refreshTmp()
        initSpinner()
        initTranslating()
    }

    fun onFontSizeChanged() {
        if (binding == null) return
        fontSizeCB(binding!!.tranTv, R.dimen.main_text_size)
        fontSizeCB(binding!!.selectTv, R.dimen.main_text_size2)
        fontSizeCB(binding!!.leftLangTv, R.dimen.main_text_size)
        adapter.notifyDataSetChanged()

        fontSizeCB(binding!!.rightLangTv, R.dimen.main_text_size)
        fontSizeCB(binding!!.leftLang2Tv, R.dimen.main_text_size)
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
        val arr: ArrayList<TransTextBean> = arrayListOf()
        val b1 = TransTextBean()
        b1.text = "77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777"
        b1.transText = "？？？？？？？？？？？？？？？555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555"
        val b2 = TransTextBean()
        b2.text = "？？？？？？？？？？？？？？？555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555"
        b2.transText = "77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777"
        b1.isMaster = false

        arr.add(b1)
        arr.add(b2)

        adapter2 = TransTextAdapter(arr, TmpServiceDelegate.getInstance().getMoreDisplayObs()?.get() ?: false, false) { v, id ->
            fontSizeCB(v, id)
        }
        binding?.dataRv?.adapter = adapter2
        val layoutManager = LinearLayoutManager(context)
        binding?.dataRv?.layoutManager = layoutManager
    }

    fun refreshLanguage() {
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
