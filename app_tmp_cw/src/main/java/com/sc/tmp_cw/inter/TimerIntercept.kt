package com.sc.tmp_cw.inter

/**
 * 定时器到达时,子页面若存在浮层等界面,优先关闭该界面并重新计时,
 * 而不是直接退出整个页面;再次计时到达才走默认退出流程
 */
interface TimerIntercept {

    /**
     * 定时器到达回调
     * @return true表示已消费本次定时(关闭了界面),需要重新计时;
     *         false表示未消费,走默认退出流程
     */
    fun onTimerReach(): Boolean

}
