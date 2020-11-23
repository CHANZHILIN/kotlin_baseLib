package com.kotlin_baselib.entity

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

/**
 * @Description:
 * @Author:         CHEN
 * @CreateDate:     2020/11/23
 */
class GraffitiEntity(lastPaint: Paint?) {
    var path: Path = Path() // 涂鸦轨迹
    val paint: Paint = Paint().apply {
        // 设置画笔
        color = lastPaint?.color ?: Color.RED
        style = lastPaint?.style ?: Paint.Style.STROKE
        strokeWidth = lastPaint?.strokeWidth ?: 5f
        isAntiAlias = lastPaint?.isAntiAlias ?: true
        strokeCap = lastPaint?.strokeCap ?: Paint.Cap.ROUND
    }

    var mX = 0f
    var mY = 0f// 轨迹偏移值
}