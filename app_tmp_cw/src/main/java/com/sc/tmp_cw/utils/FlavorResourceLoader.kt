package com.sc.tmp_cw.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.sc.tmp_cw.BuildConfig

/**
 * 风味资源加载工具类
 * 用于根据不同风味加载不同的图片资源
 */
object FlavorResourceLoader {
    
    /**
     * 根据风味获取 Drawable 资源 ID
     * 
     * @param context 上下文
     * @param flavorADrawableId Flavor A 的资源 ID
     * @param flavorBDrawableId Flavor B 的资源 ID
     * @return 对应风味的资源 ID
     */
    fun getDrawableByFlavor(
        context: Context,
        flavorADrawableId: Int,
        flavorBDrawableId: Int
    ): Drawable? {
        val drawableId = if (FlavorConfigUtil.isFlavorA()) {
            flavorADrawableId
        } else {
            flavorBDrawableId
        }
        return ContextCompat.getDrawable(context, drawableId)
    }
    
    /**
     * 根据风味获取资源 ID
     * 
     * @param flavorAResId Flavor A 的资源 ID
     * @param flavorBResId Flavor B 的资源 ID
     * @return 对应风味的资源 ID
     */
    fun getResourceIdByFlavor(
        flavorAResId: Int,
        flavorBResId: Int
    ): Int {
        return if (FlavorConfigUtil.isFlavorA()) {
            flavorAResId
        } else {
            flavorBResId
        }
    }
    
    /**
     * 根据风味和资源名称获取资源 ID
     * 
     * @param context 上下文
     * @param resourceName 资源名称（不含扩展名）
     * @param resourceType 资源类型（如 "drawable", "mipmap"）
     * @return 资源 ID，未找到返回 0
     */
    fun getResourceIdByName(
        context: Context,
        resourceName: String,
        resourceType: String = "drawable"
    ): Int {
        // 根据风味添加后缀
        val suffix = if (FlavorConfigUtil.isFlavorA()) "_a" else "_b"
        val fullName = "${resourceName}${suffix}"
        
        return context.resources.getIdentifier(
            fullName,
            resourceType,
            context.packageName
        )
    }
}
