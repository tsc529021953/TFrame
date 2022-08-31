package com.lib.network.vo

import java.io.Serializable

/**
 * @Author qiukeling
 * @Date 2020-02-21-15:08
 * @Email qiukeling@nbhope.cn
 */
class MiguScene : Serializable {
    var cataName: String = ""
    var refrenceId: Long = 0

    constructor()

    constructor(cataName: String, refrenceId: Long) {
        this.cataName = cataName
        this.refrenceId = refrenceId
    }
}