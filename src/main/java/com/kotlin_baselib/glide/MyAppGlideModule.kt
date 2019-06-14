package com.kotlin_baselib.glide

import android.content.Context
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 *  Created by CHEN on 2019/6/13
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.glide
 *  Introduce: Glide自定义module，这里可以替换联网组件，以及一些配置
 *  Glide 4.8 可以使用@GlideModule进行注解，无需再manifest中进行声明了
 **/
@GlideModule
class MyAppGlideModule : AppGlideModule() {

    val DISK_CACHE_NAME = "deepinSoul_image"
    val DISK_CACHE_SIZE: Long = 500 * 1024 * 1024    //默认缓存为250M,这里加大为500M

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        if (Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED))        //存在外部内存卡
            builder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(context, DISK_CACHE_NAME, DISK_CACHE_SIZE))
        else           //不存在外部内存卡
            builder.setDiskCache(InternalCacheDiskCacheFactory(context, DISK_CACHE_NAME, DISK_CACHE_SIZE))
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))     //设置图片的质量，如果性能较差，可换回PREFER_RGB_565
    }


    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(ProgressInterceptor())   //添加拦截器
        val okHttpClient = builder.build()
        Log.d("MyProgressListener:","到这里了")
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpGlideUrlLoader.Factory(okHttpClient)
        )    //替换联网模式为OkHttp
    }


    override fun isManifestParsingEnabled(): Boolean {
        return false   //返回false,跳过Manifest检查，返回true,进行Manifest检查，主要是兼容Glide3和Glide4版本
    }

}