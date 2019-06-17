package com.kotlin_baselib.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout

/**
 * Introduce :
 *
 *
 * 全屏模式下，即使将activity的windowSoftInputMode的属性设置为：adjustResize，
 * 在键盘显示时它未将Activity的Screen向上推动，所以你Activity的view的根树的尺寸是没有变化的。
 * 在这种情况下，你也就无法得知键盘的尺寸，对根view的作相应的推移。全屏下的键盘无法Resize的问题从2.1就已经存在了，直到现在google还未给予解决。
 * 有人已经封装好了该类，你只需引用就OK了。
 * 使用方法
 * 在你的Activity的oncreate()方法里调用AndroidBug5497Workaround.assistActivity(this);即可。
 * 注意：在setContentView(R.layout.xxx)之后调用。
 *
 *
 * 此类解决了，当activity设置了全屏，布局无法随着软键盘的弹起而向上推动
 * 坑1：全屏属性和属性adjustResize冲突
 * 坑2：如果你的APP设置了沉浸式状态栏（4.4及之后支持）的话这样使用还是会有问题，
 * 如果你的手机Android版本是4.4之前的话，你会发现布局显示不全，屏幕底部的控件有一半在屏幕外面，
 * 如果是4.4或之后的话也有问题，软键盘和上推后的布局之间有一个大概是状态栏高度的黑色区域，
 * 为了兼容这两种情况，修改上面的类：
 * 大神：https://blog.csdn.net/plq690816/article/details/51374883
 *
 *
 *
 *
 * 分割线
 * 为了适配底部虚拟按键导航栏遮盖页面问题，做出修改
 * //alter by CHEN_ on 2018/10/24
 * Created by CHEN_ on 2018/8/11.
 */
class AndroidBugWorkaround private constructor(private val activity: Activity) {
    private val mChildOfContent: View                       //被监听的视图
    private var usableHeightPrevious: Int = 0               //视图变化前的可用高度
    private val frameLayoutParams: FrameLayout.LayoutParams

    init {
        //获取根布局
        val content = activity.findViewById<View>(android.R.id.content) as FrameLayout
        mChildOfContent = content.getChildAt(0)
        //给View添加全局的布局监听器
        mChildOfContent.viewTreeObserver.addOnGlobalLayoutListener { possiblyResizeChildOfContent() }
        frameLayoutParams = mChildOfContent.layoutParams as FrameLayout.LayoutParams
    }

    companion object {

        // For more information, see https://code.google.com/p/android/issues/detail?id=5497
        // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

        fun assistActivity(activity: Activity) {
            AndroidBugWorkaround(activity)
        }
    }

    private fun possiblyResizeChildOfContent() {
        val usableHeightNow = computeUsableHeight()
        if (usableHeightNow != usableHeightPrevious) {
            frameLayoutParams.height = usableHeightNow
            mChildOfContent.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    /**
     * 可用的高度
     */
    private fun computeUsableHeight(): Int {
        val r = Rect()
        mChildOfContent.getWindowVisibleDisplayFrame(r)
        return r.bottom
    }


}