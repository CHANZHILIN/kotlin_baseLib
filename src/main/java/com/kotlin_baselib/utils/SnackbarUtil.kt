package com.kotlin_baselib.utils

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.kotlin_baselib.R

/**
 * Created by CHEN on 2019/6/17
 * Email:1181785848@qq.com
 * Package:com.kotlin_baselib.utils
 * Introduce: SnackBar工具类
 */
object SnackBarUtil {

    const val INFO = 1
    const val CONFIRM = 2
    const val WARNING = 3
    const val ALERT = 4


    var green = -0xb350b0
    var blue = -0xde6a0d
    var orange = -0x3ef9
    var red = -0xbbcca

    /**
     * 短显示SnackBar，自定义颜色
     *
     * @param view
     * @param message
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    fun shortSnackBar(
        view: View,
        message: String,
        messageColor: Int,
        backgroundColor: Int
    ): Snackbar {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        setSnackBarColor(snackBar, messageColor, backgroundColor)
        return snackBar
    }

    /**
     * 长显示SnackBar，自定义颜色
     *
     * @param view
     * @param message
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    fun longSnackBar(
        view: View,
        message: String,
        messageColor: Int,
        backgroundColor: Int
    ): Snackbar {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        setSnackBarColor(snackBar, messageColor, backgroundColor)
        return snackBar
    }

    /**
     * 自定义时常显示SnackBar，自定义颜色
     *
     * @param view
     * @param message
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    fun indefiniteSnackBar(
        view: View,
        message: String,
        duration: Int,
        messageColor: Int,
        backgroundColor: Int
    ): Snackbar {
        val snackBar =
            Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setDuration(duration)
        setSnackBarColor(snackBar, messageColor, backgroundColor)
        return snackBar
    }

    /**
     * 短显示SnackBar，可选预设类型
     *
     * @param view
     * @param message
     * @param type
     * @return
     */
    fun shortSnackBar(view: View, message: String, type: Int): Snackbar {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        switchType(snackBar, type)
        return snackBar
    }

    /**
     * 长显示SnackBar，可选预设类型
     *
     * @param view
     * @param message
     * @param type
     * @return
     */
    fun longSnackBar(view: View, message: String, type: Int): Snackbar {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        switchType(snackBar, type)
        return snackBar
    }

    /**
     * 自定义时常显示SnackBar，可选预设类型
     *
     * @param view
     * @param message
     * @param type
     * @return
     */
    fun indefiniteSnackBar(view: View, message: String, duration: Int, type: Int): Snackbar {
        val snackBar =
            Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setDuration(duration)
        switchType(snackBar, type)
        return snackBar
    }

    //选择预设类型
    private fun switchType(snackBar: Snackbar, type: Int) {
        when (type) {
            INFO -> setSnackBarColor(snackBar, blue)
            CONFIRM -> setSnackBarColor(snackBar, green)
            WARNING -> setSnackBarColor(snackBar, orange)
            ALERT -> setSnackBarColor(snackBar, Color.YELLOW, red)
        }
    }

    /**
     * 设置SnackBar背景颜色
     *
     * @param snackBar
     * @param backgroundColor
     */
    private fun setSnackBarColor(snackBar: Snackbar, backgroundColor: Int) {
        val view = snackBar.view
        view.setBackgroundColor(backgroundColor)
    }

    /**
     * 设置SnackBar文字和背景颜色
     *
     * @param snackBar
     * @param messageColor
     * @param backgroundColor
     */
    private fun setSnackBarColor(snackBar: Snackbar, messageColor: Int, backgroundColor: Int) {
        val view = snackBar.view
        view.setBackgroundColor(backgroundColor)
        (view.findViewById<View>(R.id.snackbar_text) as TextView).setTextColor(messageColor)
    }

    /**
     * 向SnackBar中添加view
     *
     * @param snackBar
     * @param layoutId
     * @param index    新加布局在SnackBar中的位置
     */
    fun snackBarAddView(snackBar: Snackbar, layoutId: Int, index: Int) {
        val snackBarView = snackBar.view
        val snackBarLayout = snackBarView as Snackbar.SnackbarLayout
        val addView = LayoutInflater.from(snackBarView.getContext()).inflate(layoutId, null)
        val p =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        p.gravity = Gravity.CENTER_VERTICAL
        snackBarLayout.addView(addView, index, p)
    }

}