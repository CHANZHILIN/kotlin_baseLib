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
 * Introduce: Snackbar工具类
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
     * 短显示Snackbar，自定义颜色
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
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        setSnackBarColor(snackbar, messageColor, backgroundColor)
        return snackbar
    }

    /**
     * 长显示Snackbar，自定义颜色
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
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        setSnackBarColor(snackbar, messageColor, backgroundColor)
        return snackbar
    }

    /**
     * 自定义时常显示Snackbar，自定义颜色
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
        val snackbar =
            Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setDuration(duration)
        setSnackBarColor(snackbar, messageColor, backgroundColor)
        return snackbar
    }

    /**
     * 短显示Snackbar，可选预设类型
     *
     * @param view
     * @param message
     * @param type
     * @return
     */
    fun shortSnackBar(view: View, message: String, type: Int): Snackbar {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        switchType(snackbar, type)
        return snackbar
    }

    /**
     * 长显示Snackbar，可选预设类型
     *
     * @param view
     * @param message
     * @param type
     * @return
     */
    fun longSnackBar(view: View, message: String, type: Int): Snackbar {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        switchType(snackbar, type)
        return snackbar
    }

    /**
     * 自定义时常显示Snackbar，可选预设类型
     *
     * @param view
     * @param message
     * @param type
     * @return
     */
    fun indefiniteSnackBar(view: View, message: String, duration: Int, type: Int): Snackbar {
        val snackbar =
            Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setDuration(duration)
        switchType(snackbar, type)
        return snackbar
    }

    //选择预设类型
    private fun switchType(snackbar: Snackbar, type: Int) {
        when (type) {
            INFO -> setSnackBarColor(snackbar, blue)
            CONFIRM -> setSnackBarColor(snackbar, green)
            WARNING -> setSnackBarColor(snackbar, orange)
            ALERT -> setSnackBarColor(snackbar, Color.YELLOW, red)
        }
    }

    /**
     * 设置Snackbar背景颜色
     *
     * @param snackbar
     * @param backgroundColor
     */
    fun setSnackBarColor(snackbar: Snackbar, backgroundColor: Int) {
        val view = snackbar.view
        view.setBackgroundColor(backgroundColor)
    }

    /**
     * 设置Snackbar文字和背景颜色
     *
     * @param snackbar
     * @param messageColor
     * @param backgroundColor
     */
    fun setSnackBarColor(snackbar: Snackbar, messageColor: Int, backgroundColor: Int) {
        val view = snackbar.view
        view.setBackgroundColor(backgroundColor)
        (view.findViewById<View>(R.id.snackbar_text) as TextView).setTextColor(messageColor)
    }

    /**
     * 向Snackbar中添加view
     *
     * @param snackbar
     * @param layoutId
     * @param index    新加布局在Snackbar中的位置
     */
    fun snackBarAddView(snackbar: Snackbar, layoutId: Int, index: Int) {
        val snackbarview = snackbar.view
        val snackbarLayout = snackbarview as Snackbar.SnackbarLayout
        val add_view = LayoutInflater.from(snackbarview.getContext()).inflate(layoutId, null)
        val p =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        p.gravity = Gravity.CENTER_VERTICAL
        snackbarLayout.addView(add_view, index, p)
    }

}