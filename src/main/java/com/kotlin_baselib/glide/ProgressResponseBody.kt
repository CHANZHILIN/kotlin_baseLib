package com.kotlin_baselib.glide

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

import java.io.IOException

/**
 *  Created by CHEN on 2019/6/14
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.glide
 *  Introduce: 继承自OkHttp的ResponseBody
 * 主要拦截图片获取的字节数和总字节数长度计算下载进度的逻辑
 **/
class ProgressResponseBody(url: String, private val responseBody: ResponseBody) : ResponseBody() {

    private val TAG = "ProgressResponseBody"

    private var bufferedSource: BufferedSource? = null

    private var listener: ProgressListener? = null

    init {
        listener = ProgressInterceptor.LISTENER_MAP.get(url)
    }//OkHttp拦截到的原始的ResponseBody对象

    override fun contentType(): MediaType? {        //直接调用原始的contentType()就可以
        return responseBody.contentType()
    }

    override fun contentLength(): Long { //直接调用原始的contentLength()就可以
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {    //加入下载进度计算逻辑
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(ProgressSource(responseBody.source()))
        }
        return bufferedSource!!
    }

    /**
     * 自定义的继承自ForwardingSource的实现类。
     * ForwardingSource也是一个使用委托模式的工具，它不处理任何具体的逻辑，只是负责将传入的原始Source对象进行中转。
     * 但是，我们使用ProgressSource继承自ForwardingSource，那么就可以在中转的过程中加入自己的逻辑了
     */
    private inner class ProgressSource internal constructor(source: Source) : ForwardingSource(source) {

        internal var totalBytesRead: Long = 0

        internal var currentProgress: Int = 0

        @Throws(IOException::class)
        override fun read(sink: Buffer, byteCount: Long): Long {
            val bytesRead = super.read(sink, byteCount)   //读取到的字节数
            val fullLength = responseBody.contentLength() //下载文件的总字节数
            if (bytesRead.toInt() == -1) {
                totalBytesRead = fullLength
            } else {
                totalBytesRead += bytesRead
            }
            val progress = (100f * totalBytesRead / fullLength).toInt()
            //            Log.d(TAG, "download progress is " + progress);
            if (listener != null && progress != currentProgress) {
                listener!!.onProgress(progress)
            }
            if (listener != null && totalBytesRead == fullLength) {
                listener = null
            }
            currentProgress = progress
            return bytesRead
        }
    }

}
