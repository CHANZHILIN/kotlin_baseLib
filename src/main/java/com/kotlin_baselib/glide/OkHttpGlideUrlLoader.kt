package com.kotlin_baselib.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.OkHttpClient

import java.io.InputStream

/**
 *  Created by CHEN on 2019/6/14
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.glide
 *  Introduce:自定义GlideUrlLoader,这里替换了OkHttp的联网方式
 *  并将OkHttp获取到的数据传递过来
 *  Glide的网络通讯逻辑是由HttpUrlGlideUrlLoader.Factory来负责
 **/
class OkHttpGlideUrlLoader(private val okHttpClient: OkHttpClient) : ModelLoader<GlideUrl, InputStream> {

    override fun buildLoadData(
        url: GlideUrl,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(url, OkHttpFetcher(okHttpClient, url))
    }

    override fun handles(url: GlideUrl): Boolean {
        return true    //Returns true if the given model is a of a recognized type that this loader can probably load.
    }

    /**
     * 通过这个静态内部类Factory去替换联网组件
     */
    class Factory(client: OkHttpClient) : ModelLoaderFactory<GlideUrl, InputStream> {
        private var client: OkHttpClient? = client
        private val okHttpClient: OkHttpClient
            @Synchronized get() {
                if (client == null) {
                    client = OkHttpClient()
                }
                return client!!
            }

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpGlideUrlLoader(okHttpClient)
        }

        override fun teardown() {}
    }


}
