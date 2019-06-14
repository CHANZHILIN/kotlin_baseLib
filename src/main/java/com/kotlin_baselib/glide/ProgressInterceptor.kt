package com.kotlin_baselib.glide

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 *  Created by CHEN on 2019/6/14
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.glide
 *  Introduce: 图片加载进度拦截器
 **/
class ProgressInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val url = request.url().toString()
        val body = response.body()    //原始的ResponseBody对象
        return response.newBuilder().body(ProgressResponseBody(url, body!!)).build()
    }

    companion object ListenerManager{

        //使用Map进行url的管理，会使用Glide同时加载很多张图片，而这种情况下，必须要能区分出来每个下载进度的回调到底是对应哪个图片URL地址的
         val LISTENER_MAP: MutableMap<String, ProgressListener> = HashMap()

        fun addListener(url: String, listener: ProgressListener) {
            LISTENER_MAP[url] = listener
        }

        fun removeListener(url: String) {
            LISTENER_MAP.remove(url)
        }

    }
}
