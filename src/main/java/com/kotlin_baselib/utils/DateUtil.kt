package com.kotlin_baselib.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


/**
 *  Created by CHEN on 2019/7/4
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.utils
 *  Introduce: 时间转换工具
 **/
@SuppressLint("SimpleDateFormat")
object DateUtil {
    const val yyyyMMddHHmmss = "yyyyMMddHHmmss"
    const val hhmmss = "HH:mm:ss"


    fun getNowTime(type: String): String {
        val nowDate = Date()
        val now = Calendar.getInstance()
        now.time = nowDate
        val formatter = SimpleDateFormat(type)
        return formatter.format(now.time)
    }


    fun parseToString(currentTime: Long, type: String): String {
        val curDate = Date(currentTime)//获取时间
        val formatter = SimpleDateFormat(type)
        return formatter.format(curDate.time)
    }

    fun getFormatHMS(time: Long): String {
        var formatTime = time
        formatTime /= 1000
        val s = (formatTime % 60).toInt()
        val m = (formatTime / 60).toInt()
        val h = (formatTime / 3600).toInt()
        return String.format("%02d:%02d:%02d", h, m, s)

    }


    fun formatSecond(second: Int): String {
        var html = "0"
        val hours = second / (60 * 60)
        val minutes = second / 60 - hours * 60
        val seconds = second - minutes * 60 - hours * 60 * 60
        html = if (hours > 0) {
            if (hours >= 10) {
                hours.toString() + ""
            } else {
                "0$hours"
            }
        } else {
            "00"
        }
        html = if (minutes > 0) {
            if (minutes >= 10) {
                "$html:$minutes"
            } else {
                "$html:0$minutes"
            }
        } else {
            "$html:00"
        }
        html = if (seconds >= 10) {
            "$html:$seconds"
        } else {
            "$html:0$seconds"
        }
        return html

    }
}