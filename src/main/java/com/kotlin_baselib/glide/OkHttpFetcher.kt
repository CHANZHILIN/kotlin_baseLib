package com.kotlin_baselib.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.util.ContentLengthInputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

import java.io.IOException
import java.io.InputStream

/**
 *  Created by CHEN on 2019/6/14
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.glide
 *  Introduce: 自定义fetcher,主要进行OkHttp的设置
 *  跟Glide 3.X 版本有不同，主要根据DataFetcher的子类照葫芦画瓢
 **/

class OkHttpFetcher(private val client: OkHttpClient, private val url: GlideUrl) : DataFetcher<InputStream> {
    private var stream: InputStream? = null
    private var responseBody: ResponseBody? = null
    @Volatile
    private var isCancelled: Boolean = false

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val requestBuilder = Request.Builder()
            .url(url.toStringUrl())
        for ((key, value) in url.headers) {
            requestBuilder.addHeader(key, value)
        }
        requestBuilder.addHeader("HttpLib", "OkHttp")
        //        Log.d("OkHttpFetcher",url+"获取成功");
        val request = requestBuilder.build()
        if (isCancelled) {
            return
        }
        var response: Response? = null
        try {
            response = client.newCall(request).execute()
            responseBody = response!!.body()
            if (!response.isSuccessful || responseBody == null) {
                throw IOException("Request failed with code: " + response.code())
            }
            stream = ContentLengthInputStream.obtain(
                responseBody!!.byteStream(),
                responseBody!!.contentLength()
            )
            callback.onDataReady(stream)
        } catch (e: IOException) {
            e.printStackTrace()
            callback.onLoadFailed(e)
        }

    }

    override fun cleanup() {
        try {
            if (stream != null) {
                stream!!.close()
            }
            if (responseBody != null) {
                responseBody!!.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    override fun cancel() {
        isCancelled = true
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }
}


