package com.kotlin_baselib.media.decoder

/**
 *  Created by CHEN on 2019/10/22
 *  Email:1181785848@qq.com
 *  Introduce:
 **/
interface IDecoderProgress {

    /**
     * 视频宽高回调
     */
    fun videoSizeChange(width: Int, height: Int, rotationAngle: Int)

    /**
     * 视频播放进度回调
     */
    fun videoProgressChange(pos: Long)
}