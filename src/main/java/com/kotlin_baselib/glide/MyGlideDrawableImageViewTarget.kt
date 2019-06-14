package com.kotlin_baselib.glide

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition

/**
 *  Created by CHEN on 2019/6/14
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.glide
 *  Introduce: 自定义ViewTarget，可获取到显示图片的drawable对象
 **/
class MyGlideDrawableImageViewTarget(
    view: ImageView,
    private val mProgressBar: CircleProgressBar,
    private val mUrl: String
) : DrawableImageViewTarget(view) {

    override fun onLoadStarted(placeholder: Drawable?) {
        super.onLoadStarted(placeholder)
        mProgressBar.visibility = View.VISIBLE
        mProgressBar.setProgress(0)

    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)

        mProgressBar.visibility = View.GONE
        ProgressInterceptor.removeListener(mUrl)

    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        super.onResourceReady(resource, transition)
        mProgressBar.visibility = View.GONE
        ProgressInterceptor.removeListener(mUrl)

    }

}
