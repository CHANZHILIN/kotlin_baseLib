package com.kotlin_baselib.glide

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kotlin_baselib.R

/**
 *  Created by CHEN on 2019/6/14
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.glide
 *  Introduce: Glile封装
 **/
class GlideUtil private constructor() {

    private object ImageLoaderInstance {
        val instance = GlideUtil()
    }

    companion object {

        val instance: GlideUtil
            get() = ImageLoaderInstance.instance
    }


    /**
     * 加载图片
     */
    fun loadImage(context: Context, path: String, target: ImageView) {
        val options = RequestOptions()
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)

        GlideApp.with(context)
            .load(path)
            .apply(options)
            .into(target)
    }

    /**
     * 带进度条的图片加载
     */
    fun loadImageWithProgress(context: Context, path: String, target: ProgressImageView) {
        ProgressInterceptor.addListener(path, MyProgressListener(target.circleProgressBar)) //设置监听
        val options = RequestOptions()
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)

        GlideApp.with(context)
            .load(path)
            .apply(options)
            .into(MyGlideDrawableImageViewTarget(target.imageView, target.circleProgressBar, path))
    }


    /**
     * 不设置缓存
     */
    fun loadImageWithoutCache(context: Context, path: String, target: ImageView) {
        val options = RequestOptions()
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .skipMemoryCache(true)      //关闭内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE)     //关闭磁盘缓存

        GlideApp.with(context)
            .load(path)
            .apply(options)
            .into(target)
    }


}
