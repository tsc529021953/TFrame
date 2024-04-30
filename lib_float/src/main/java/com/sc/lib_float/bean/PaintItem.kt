package com.sc.lib_float.bean

import android.graphics.*

/**
 * @author  tsc
 * @date  2024/4/8 14:37
 * @version 0.0.0-1
 * @description
 */
class PaintItem {

    constructor(paint: Paint, path: Path, pointerId: Int) {
        this.paint = paint
        this.path = path
        this.pointerId = pointerId
    }

    var pointerId: Int = 0

    var paint: Paint? = null

    var path: Path? = null

    var x: Float = 0F
    var y: Float = 0F

    override fun toString(): String {
        return "PaintItem $x $y"
    }
}
