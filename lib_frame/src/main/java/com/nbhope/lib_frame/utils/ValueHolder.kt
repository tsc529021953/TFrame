package com.nbhope.lib_frame.utils

import android.os.Handler
import android.os.Looper
import java.math.BigDecimal

class ValueHolder (private var callback: ((da: Int) -> Unit)? = null) {

    private val handler = Handler(Looper.getMainLooper())
    private var value: Int? = null
    private var runnable: Runnable? = null

    /**
     * 用于获取最后一次的结果
     */
    fun setValue(newValue: Int, timer: Long = 1000) {
        value = newValue
        runnable?.let {
            handler.removeCallbacks(it)
        }
        runnable = Runnable {
            callback?.invoke(value!!)
            runnable = null
        }
        handler.postDelayed(runnable!!, timer)
    }

    fun stop() {
        runnable?.let {
            handler.removeCallbacks(it)
        }
        runnable = null
    }

    /**
     * 时限内只允许执行一次
     */
    companion object{
        private val handler = Handler(Looper.getMainLooper())
        private var value: Int? = null
        private var runnable: Runnable? = null
        private var callback: (() -> Unit)? = null

        fun setValue(timer: Long = 1000, callback: (() -> Unit)? = null) {
//        runnable?.let {
//            handler.removeCallbacks(it)
//        }
            if (runnable != null) return
            this.callback = callback
            runnable = Runnable {
                runnable = null
            }
            this.callback?.invoke()
            handler.postDelayed(runnable!!, timer)
        }


        /**
         * 将值从一个区间映射到另一个区间
         * @param value 需要映射的值
         * @param fromRangeStart 原始区间的起始值
         * @param fromRangeEnd 原始区间的结束值
         * @param toRangeStart 目标区间的起始值
         * @param toRangeEnd 目标区间的结束值
         * @return 映射后的值
         */
        fun mapValueFromRangeToRange(
            value: Int,
            fromRangeStart: Int,
            fromRangeEnd: Int,
            toRangeStart: Int,
            toRangeEnd: Int
        ): Int {
            val fromRange = BigDecimal(fromRangeEnd - fromRangeStart)
            val toRange = BigDecimal(toRangeEnd - toRangeStart)
            val valueBigDecimal = BigDecimal(value)
            val mappedValue = valueBigDecimal.subtract(BigDecimal(fromRangeStart))
                .multiply(toRange)
                .divide(fromRange, 10, BigDecimal.ROUND_HALF_UP)
                .add(BigDecimal(toRangeStart))
            return mappedValue.setScale(0, BigDecimal.ROUND_HALF_UP).toInt()
        }
    }
}
