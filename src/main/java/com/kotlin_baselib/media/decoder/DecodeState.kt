package com.kotlin_baselib.media.decoder

/**
 *  Created by CHEN on 2019/10/22
 *  Email:1181785848@qq.com
 *  Introduce:解码状态
 **/
enum class DecodeState {
    /**开始状态*/
    START,
    /**解码中*/
    DECODING,
    /**解码暂停*/
    PAUSE,
    /**正在快进*/
    SEEKING,
    /**解码完成*/
    FINISH,
    /**解码器停止释放*/
    STOP
}
