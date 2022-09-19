package com.nbhope.app.uhome.util

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

/**
 * @author  tsc
 * @date  2022/9/11 15:02
 * @version 0.0.0-1
 * @description
 */
object RGB2HSLUtils {

    class HSL constructor(var h: Float,var s: Float,var l: Float){
        override fun toString(): String {
            return "HSL(h=$h, s=$s, l=$l)"
        }
    }

    class RGB constructor(var r: Int,var g: Int,var b: Int){
        override fun toString(): String {
            return "RGB(r=$r, g=$g, b=$b)"
        }
    }

    fun rgb2hsl(rgb: RGB): HSL {
        return rgb2hsl(rgb.r, rgb.g, rgb.b)
    }

    fun rgb2hsl(rC: Int, gC: Int, bC: Int): HSL {
        var r = rC / 255.0f
        var g = gC / 255.0f
        var b = bC / 255.0f
        var max = max(max(r, g), b)
        var min = min(min(r, g), b);
        var v = (max + min) / 2.0f
        var h = v
        var s = v
        var l = v

        if(max == min){
            h = 0f
            s = 0f
        }else{
            var d = max - min;
            s = if (l > 0.5) d / (2f - max - min) else d / (max + min);
            when (max) {
                r -> h = (g - b) / d + (if (g < b) 6 else 0)
                g -> h = (b - r) / d + 2
                b -> h = (r - g) / d + 4
            }
            h /= 6.0f;
        }
        var hsl = HSL(floor(h * 100), round(s * 100), round(l * 100))
        return hsl
    }

    fun hsl2rgb(hsl: HSL) {

    }

}