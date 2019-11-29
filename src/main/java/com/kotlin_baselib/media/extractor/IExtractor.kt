package com.kotlin_baselib.media.extractor

import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 *  Created by CHEN on 2019/10/22
 *  Email:1181785848@qq.com
 *  Introduce:音视频分离器
 **/
interface IExtractor {
    /**
     * 获取视频格式
     */
    fun getFormat(): MediaFormat?

    /**
     * 读取音视频数据
     */
    fun readBuffer(byteBuffer: ByteBuffer): Int

    /**
     * 获取当前帧时间
     */
    fun getCurrentTimestamp(): Long

    fun getSampleFlag(): Int

    /**
     * seek到指定位置，并返回实际帧的时间戳
     */
    fun seek(pos: Long): Long

    fun setStartPos(pos: Long)

    /**
     * 停止读取数据
     */
    fun stop()
}