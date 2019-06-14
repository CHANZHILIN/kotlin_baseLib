package com.kotlin_baselib.loadingview

import android.animation.Animator

/**
 *  Created by CHEN on 2019/6/14
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.loadingview
 *  Introduce: 动画监听
 **/
abstract class AnimatorListener : Animator.AnimatorListener {

    override fun onAnimationStart(animation: Animator) {

    }

    override fun onAnimationCancel(animation: Animator) {

    }

    override fun onAnimationRepeat(animation: Animator) {

    }
}