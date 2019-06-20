package com.kotlin_baselib.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue

/**
 *  Created by CHEN on 2019/6/20
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.utils
 *  Introduce:  尺寸转换工具
 **/
object  Dp2PxUtil {

    fun dip2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue, context.resources.displayMetrics
        ).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt().toFloat()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 获取屏幕宽，单位为dip
     *
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高，单位为dip
     *
     * @return
     */
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 获取屏幕高度，包括底部导航栏
     *
     * @param activity
     * @return
     */
    fun getRealScreenHeight(activity: Activity): Int {
        val windowManager = activity.windowManager
        val display = windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(displayMetrics)
        } else {
            display.getMetrics(displayMetrics)
        }
        return displayMetrics.heightPixels
    }

    /**
     * 用于获取状态栏的高度。 使用Resource对象获取（推荐这种方式）
     *
     * @return 返回状态栏高度的像素值。
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier(
            "status_bar_height", "dimen",
            "android"
        )
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}