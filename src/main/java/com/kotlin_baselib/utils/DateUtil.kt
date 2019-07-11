package com.kotlin_baselib.utils

import android.annotation.SuppressLint
import java.sql.Timestamp
import java.text.ParseException
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


    fun getNowTime(type:String): String {
        val nowDate = Date()
        val now = Calendar.getInstance()
        now.setTime(nowDate)
        val formatter = SimpleDateFormat(type)
        return formatter.format(now.getTime())
    }


    fun parseToString(curentTime: Long,type:String): String {
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
}