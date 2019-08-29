package com.kotlin_baselib.utils

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 *  Created by CHEN on 2019/8/7
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.utils
 *  Introduce:  图片操作
 **/
object BitmapUtil {

    /**
     * 多张图片横向拼接
     * @param picPaths
     * @return
     */
    fun addHBitmap(bits: List<Bitmap>?): Bitmap? {
        var firstBit: Bitmap? = null
        if (!bits.isNullOrEmpty()) {
            firstBit = bits[0]
            for (i in 1 until bits.size) {
                firstBit = addHBitmap(firstBit!!, bits[i])
            }
        }
        return firstBit

    }


    private fun addHBitmap(first: Bitmap, second: Bitmap): Bitmap {
        val width = first.width + second.width
        val height = Math.max(first.height, second.height)
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(result)
        canvas.drawBitmap(first, 0f, 0f, null)
        canvas.drawBitmap(second, first.width.toFloat(), 0f, null)
        return result
    }
}