package com.sc.tmp_translate.view

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.sc.tmp_translate.R
import com.sc.tmp_translate.databinding.DisplayOtherBinding

class OtherDisplay(context: Context, display: Display) : Presentation(context, display) {

    private var mBinding: DisplayOtherBinding? = null

    var number = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.display_other,
            null,
            false
        )
        log("onCreate $mBinding ${mBinding?.root}")
        mBinding?.root?.let { setContentView(it) }
        mBinding?.numTv?.text = number.toString()
        val cb = {
            number++
            mBinding?.numTv?.text = number.toString()
        }
        mBinding?.numBtn?.setOnClickListener {
            cb.invoke()
        }
    }

    fun log(msg: Any?) {
        System.out.println("异显：${msg ?: "null"}")
    }
}
