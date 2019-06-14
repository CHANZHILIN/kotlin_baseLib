package com.kotlin_baselib.glide

import android.util.Log

/**
 *  Created by CHEN on 2019/6/14
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.glide
 *  Introduce: 图片加载进度监听
 **/
class MyProgressListener(private var mProgressBar: CircleProgressBar) : ProgressListener {

    override fun onProgress(progress: Int) {
        mProgressBar.setProgress(progress)
    }
}
