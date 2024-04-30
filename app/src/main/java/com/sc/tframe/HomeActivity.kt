package com.sc.tframe

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.nbhope.lib_frame.utils.ViewUtil.Companion.immersionTitle
import com.nbhope.lib_frame.widget.Rotate3dAnimation
import timber.log.Timber

/**
 * @author  tsc
 * @date  2024/4/15 17:37
 * @version 0.0.0-1
 * @description
 */
class HomeActivity : AppCompatActivity() {

    companion object{
        const val ANIMTION_TIMER = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        immersionTitle(this)
        animationTest()


    }

    fun animationTest() {
        val view2 = findViewById<View>(R.id.test_ly)
        view2.visibility = View.VISIBLE
        view2.setOnClickListener {
            view2.visibility = View.GONE
        }
        animationTest2()

        val view = findViewById<ImageView>(R.id.test_iv)
        view.setOnClickListener {
            view2.visibility = View.VISIBLE
        }
        val centerX: Float = 100f
        val centerY: Float = 100f
        val centerZ = 0f
        val rotate3dAnimation =
            Rotate3dAnimation(-360f, 0f, centerX, centerY, centerZ, Rotate3dAnimation.ROTATE_Y_AXIS, true)
        rotate3dAnimation!!.duration = ANIMTION_TIMER
        rotate3dAnimation!!.repeatCount = ObjectAnimator.INFINITE
        rotate3dAnimation?.repeatMode = ObjectAnimator.RESTART
        view.startAnimation(rotate3dAnimation!!)

        val animation = AnimationSet(true)
        animation.addAnimation(rotate3dAnimation!!)
        animation.duration = ANIMTION_TIMER
        animation.startOffset = ANIMTION_TIMER
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = ObjectAnimator.INFINITE
        animation?.repeatMode = ObjectAnimator.RESTART
        view.startAnimation(animation)
        Timber.i("KTAG 开启动画")
    }

    fun animationTest2() {
        val view = findViewById<ImageView>(R.id.test_iv2)
        val centerX: Float = 150f
        val centerY: Float = 150f
        val centerZ = 0f
        val rotate3dAnimation =
            Rotate3dAnimation(-360f, 0f, centerX, centerY, centerZ, Rotate3dAnimation.ROTATE_Y_AXIS, true)
        rotate3dAnimation!!.duration = ANIMTION_TIMER
        rotate3dAnimation!!.repeatCount = ObjectAnimator.INFINITE
        rotate3dAnimation?.repeatMode = ObjectAnimator.RESTART
        view.startAnimation(rotate3dAnimation!!)

        val animation = AnimationSet(true)
        animation.addAnimation(rotate3dAnimation!!)
        animation.duration = ANIMTION_TIMER
        animation.startOffset = ANIMTION_TIMER
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = ObjectAnimator.INFINITE
        animation?.repeatMode = ObjectAnimator.RESTART
        view.startAnimation(animation)
        Timber.i("KTAG 开启动画")
    }

}
