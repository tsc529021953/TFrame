package com.sc.xs_cc.service

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @Author qiukeling
 * @Date 2020/5/18-2:54 PM
 * @Email qiukeling@nbhope.cn
 *
 * 1. 开启组播
 */
interface MainService : IProvider {

    fun reTimer()
    fun stopTimer()
}
