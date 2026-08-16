package com.sc.tframe

import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 *
 * @author sicai.tang@geely.com
 * @since 2026/4/20
 */
class ZeekrTestActivity : AppCompatActivity() {

    val imageMap = mapOf(
        "bg7.jpg" to R.drawable.bg7,
//        "bg2.webp" to R.drawable.bg2,
//        "bg4.png" to R.drawable.bg4,
//        "bg5.jpg" to R.drawable.bg5,
        "bg_fs_connect.png" to R.drawable.bg_fs_connect,
//        "bg_fs_disconnect.jpg" to R.drawable.bg_fs_disconnect,
//        "bg_fs_installing.png" to R.drawable.bg_fs_installing,
//        "bg_fs_unavailable.png" to R.drawable.bg_fs_unavailable,
        "bg8.jpg" to R.drawable.bg8,
        "bg_fs_unbonded.jpg" to R.drawable.bg_fs_unbonded
////        "bg6.webp" to R.drawable.bg6,
    )

    lateinit var ivBg: androidx.appcompat.widget.AppCompatImageView
    lateinit var ivBg2: androidx.appcompat.widget.AppCompatImageView
    lateinit var changeBgTv: androidx.appcompat.widget.AppCompatTextView
    lateinit var changeIvTv: androidx.appcompat.widget.AppCompatTextView
    lateinit var infoTv: androidx.appcompat.widget.AppCompatTextView
    lateinit var bgLy: androidx.constraintlayout.widget.ConstraintLayout

    var bgName = ""
    var ivName = ""
    var bgResId = -1
    var ivResId = -1

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zeekr_test)
        initView()
    }

    fun initView() {
        ivBg = findViewById(R.id.iv_bg)
        ivBg2 = findViewById(R.id.iv_bg2)
        changeBgTv = findViewById(R.id.change_bg_tv)
        changeIvTv = findViewById(R.id.change_iv_tv)
        infoTv = findViewById(R.id.info_tv)
        bgLy = findViewById(R.id.bg_ly)

        changeBgTv.setOnClickListener { changeBg() }
        changeIvTv.setOnClickListener { changeIv() }
        changeBg()
        changeIv()
    }

    fun changeBg() {
        bgResId++
        if (bgResId >= imageMap.size)
            bgResId = 0
        bgName = imageMap.keys.toList()[bgResId]
        bgLy.setBackgroundResource(imageMap.values.toList()[bgResId])
        ivBg.setImageResource(imageMap.values.toList()[bgResId])
        ivBg2.setImageResource(imageMap.values.toList()[bgResId])
        refreshInfo()
    }
    fun changeIv() {
        ivResId++
        if (ivResId >= 3)
            ivResId = 0
        when (ivResId) {
            0 -> {
                ivName = "FixedScaleImageView"
                ivBg.visibility = View.VISIBLE
                ivBg2.visibility = View.GONE
            }
            1 -> {
                ivName = "AppCompatImageView"
                ivBg.visibility = View.GONE
                ivBg2.visibility = View.VISIBLE
            }
            2 -> {
                ivBg.visibility = View.GONE
                ivBg2.visibility = View.GONE
                ivName = "ConstraintLayout"
            }
        }
        refreshInfo()
    }
    fun refreshInfo() {
        infoTv.text = "bg: $bgName, iv: $ivName"
    }
}