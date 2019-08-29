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
    val yyyyMMddHHmmss = "yyyyMMddHHmmss"
    val hhmmss = "HH:mm:ss"


    fun getNowTime(type: String): String {
        val nowDate = Date()
        val now = Calendar.getInstance()
        now.setTime(nowDate)
        val formatter = SimpleDateFormat(type)
        return formatter.format(now.getTime())
    }


    fun parseToString(curentTime: Long, type: String): String {
        val curDate = Date(curentTime)//获取时间
        val formatter = SimpleDateFormat(type)
        return formatter.format(curDate.getTime())
    }

    fun getFormatHMS(time: Long): String {
        var time = time
        time = time / 1000
        val s = (time % 60).toInt()
        val m = (time / 60).toInt()
        val h = (time / 3600).toInt()
        return String.format("%02d:%02d:%02d", h, m, s)

    }


    fun formatSecond(second: Int): String {
        var html = "0"
        val hours = second / (60 * 60)
        val minutes = second / 60 - hours * 60
        val seconds = second - minutes * 60 - hours * 60 * 60
        if (hours > 0) {
            if (hours >= 10) {
                html = hours.toString() + ""
            } else {
                html = "0$hours"
            }
        } else {
            html = "00"
        }
        if (minutes > 0) {
            if (minutes >= 10) {
                html = "$html:$minutes"
            } else {
                html = "$html:0$minutes"
            }
        } else {
            html = "$html:00"
        }
        if (seconds >= 10) {
            html = "$html:$seconds"
        } else {
            html = "$html:0$seconds"
        }
        return html

    }
}